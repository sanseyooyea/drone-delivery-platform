<template>
  <div class="chat-workbench">
    <h2>客服工作台</h2>
    <div class="chat-container">
      <!-- 左侧会话列表 -->
      <div class="session-panel">
        <div class="session-header">在线会话</div>
        <div class="session-list" v-loading="sessionsLoading">
          <div
            v-for="session in sessionList"
            :key="session.id"
            class="session-item"
            :class="{ active: activeSessionId === session.id }"
            @click="selectSession(session)"
          >
            <el-avatar :size="36">{{ session.userName?.charAt(0) || '用' }}</el-avatar>
            <div class="session-info">
              <div class="session-name">{{ session.userName }}</div>
              <div class="session-preview">{{ session.lastMessage || '暂无消息' }}</div>
            </div>
          </div>
          <el-empty v-if="!sessionsLoading && sessionList.length === 0" description="暂无在线会话" />
        </div>
      </div>

      <!-- 右侧聊天面板 -->
      <div class="chat-panel">
        <template v-if="activeSessionId">
          <div class="chat-header">
            <span>{{ activeSession?.userName }} 的会话</span>
            <el-button type="danger" size="small" @click="handleCloseSession">结束会话</el-button>
          </div>

          <div class="chat-messages" ref="messagesRef" v-loading="messagesLoading">
            <div
              v-for="msg in messageList"
              :key="msg.id"
              class="message-row"
              :class="{ 'is-admin': msg.senderRole === 2 }"
            >
              <div class="message-bubble">
                <div class="message-sender">{{ msg.senderRole === 2 ? '客服' : '用户' }}</div>
                <div class="message-content">{{ msg.content }}</div>
                <div class="message-time">{{ msg.sendTime }}</div>
              </div>
            </div>
            <el-empty v-if="!messagesLoading && messageList.length === 0" description="暂无消息" />
          </div>

          <div class="chat-input-area">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="请输入回复内容..."
              @keydown.ctrl.enter="handleSend"
            />
            <div class="send-bar">
              <span class="send-tip">Ctrl + Enter 发送</span>
              <el-button type="primary" :disabled="!inputMessage.trim()" @click="handleSend">发送</el-button>
            </div>
          </div>
        </template>

        <div v-else class="no-session">
          <el-empty description="请从左侧选择一个会话" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActiveChatSessions, closeChatSession } from '@/api/admin'
import { getChatMessages } from '@/api/chat'

const sessionsLoading = ref(false)
const messagesLoading = ref(false)
const sessionList = ref([])
const messageList = ref([])
const activeSessionId = ref(null)
const activeSession = ref(null)
const inputMessage = ref('')
const messagesRef = ref(null)

const fetchSessions = async () => {
  sessionsLoading.value = true
  try {
    const res = await getActiveChatSessions()
    sessionList.value = res.data
  } catch (e) {
    console.error('获取会话列表失败', e)
  } finally {
    sessionsLoading.value = false
  }
}

const selectSession = async (session) => {
  activeSessionId.value = session.id
  activeSession.value = session
  await fetchMessages()
}

const fetchMessages = async () => {
  if (!activeSessionId.value) return
  messagesLoading.value = true
  try {
    const res = await getChatMessages(activeSessionId.value)
    messageList.value = res.data
    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error('获取聊天记录失败', e)
  } finally {
    messagesLoading.value = false
  }
}

const scrollToBottom = () => {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const handleSend = () => {
  const text = inputMessage.value.trim()
  if (!text) return

  // 本地先追加消息用于展示
  messageList.value.push({
    id: Date.now(),
    senderType: 'admin',
    senderName: '客服',
    content: text,
    createTime: new Date().toLocaleString(),
  })
  inputMessage.value = ''
  nextTick(() => scrollToBottom())

  // TODO: 调用发送消息接口（WebSocket 或 HTTP）
}

const handleCloseSession = async () => {
  try {
    await ElMessageBox.confirm('确定要结束该会话吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await closeChatSession(activeSessionId.value)
    ElMessage.success('会话已结束')
    activeSessionId.value = null
    activeSession.value = null
    messageList.value = []
    fetchSessions()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('关闭会话失败', e)
    }
  }
}

onMounted(() => {
  fetchSessions()
})
</script>

<style scoped>
.chat-workbench {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-container {
  flex: 1;
  display: flex;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  min-height: 520px;
}

/* 左侧会话列表 */
.session-panel {
  width: 280px;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}

.session-header {
  padding: 14px 16px;
  font-weight: bold;
  font-size: 15px;
  border-bottom: 1px solid #e4e7ed;
  background: #fff;
}

.session-list {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.session-item:hover {
  background: #ecf5ff;
}

.session-item.active {
  background: #d9ecff;
}

.session-info {
  flex: 1;
  overflow: hidden;
}

.session-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
}

.session-preview {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 右侧聊天面板 */
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  font-weight: 500;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message-row {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 16px;
}

.message-row.is-admin {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 60%;
  padding: 10px 14px;
  border-radius: 8px;
  background: #f0f2f5;
  position: relative;
}

.message-row.is-admin .message-bubble {
  background: #409eff;
  color: #fff;
}

.message-sender {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.message-row.is-admin .message-sender {
  color: rgba(255, 255, 255, 0.75);
}

.message-content {
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message-time {
  font-size: 11px;
  color: #b0b3b8;
  margin-top: 4px;
  text-align: right;
}

.message-row.is-admin .message-time {
  color: rgba(255, 255, 255, 0.6);
}

/* 输入区域 */
.chat-input-area {
  border-top: 1px solid #e4e7ed;
  padding: 12px 16px;
}

.send-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.send-tip {
  font-size: 12px;
  color: #909399;
}

.no-session {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
