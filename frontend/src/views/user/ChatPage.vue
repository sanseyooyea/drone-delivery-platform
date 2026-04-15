<template>
  <div class="chat-page">
    <el-row :gutter="0" class="chat-container">
      <!-- 会话列表 -->
      <el-col :span="6" class="session-panel">
        <div class="session-header">
          <span>消息列表</span>
          <el-button type="primary" size="small" @click="handleNewSession">
            <el-icon><Plus /></el-icon>
            新会话
          </el-button>
        </div>
        <div class="session-list">
          <div
            v-for="session in sessions"
            :key="session.id"
            class="session-item"
            :class="{ active: activeSessionId === session.id }"
            @click="selectSession(session)"
          >
            <el-avatar :size="36">{{ session.targetName?.[0] || '客' }}</el-avatar>
            <div class="session-info">
              <div class="session-name">{{ session.targetName || '客服' }}</div>
              <div class="session-last">{{ session.lastMessage || '暂无消息' }}</div>
            </div>
            <div class="session-time">{{ session.lastTime || '' }}</div>
          </div>
          <el-empty
            v-if="sessions.length === 0"
            description="暂无会话"
            :image-size="60"
          />
        </div>
      </el-col>

      <!-- 聊天区域 -->
      <el-col :span="18" class="chat-panel">
        <template v-if="activeSessionId">
          <!-- 聊天头部 -->
          <div class="chat-header">
            <span>{{ activeSession?.targetName || '客服' }}</span>
          </div>

          <!-- 消息列表 -->
          <div class="message-area" ref="messageAreaRef">
            <div v-if="messagesLoading" class="loading-tip">
              <el-icon class="is-loading"><Loading /></el-icon>
              加载中...
            </div>
            <div
              v-for="msg in messages"
              :key="msg.id"
              class="message-row"
              :class="{ 'is-self': msg.isSelf }"
            >
              <el-avatar :size="32">
                {{ msg.isSelf ? '我' : '客' }}
              </el-avatar>
              <div class="message-bubble">
                <div class="message-text">{{ msg.content }}</div>
                <div class="message-time">{{ msg.createTime }}</div>
              </div>
            </div>
            <el-empty
              v-if="!messagesLoading && messages.length === 0"
              description="暂无消息，发送一条开始聊天吧"
              :image-size="60"
            />
          </div>

          <!-- 输入区域 -->
          <div class="input-area">
            <el-input
              v-model="inputText"
              type="textarea"
              :rows="3"
              placeholder="输入消息..."
              resize="none"
              @keydown.enter.exact.prevent="sendMessage"
            />
            <div class="input-actions">
              <el-button
                type="primary"
                :disabled="!inputText.trim()"
                @click="sendMessage"
              >
                发送
              </el-button>
            </div>
          </div>
        </template>

        <template v-else>
          <div class="no-session">
            <el-empty description="请选择一个会话或创建新会话" />
          </div>
        </template>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Loading } from '@element-plus/icons-vue'
import { createChatSession, getChatSessions, getChatMessages } from '@/api/chat'
import { getUserInfo } from '@/utils/auth'

const currentUser = getUserInfo()
const sessions = ref([])
const activeSessionId = ref(null)
const messages = ref([])
const inputText = ref('')
const messagesLoading = ref(false)
const messageAreaRef = ref(null)

const activeSession = computed(() =>
  sessions.value.find((s) => s.id === activeSessionId.value)
)

const fetchSessions = async () => {
  try {
    const res = await getChatSessions()
    sessions.value = res.data || []
  } catch (e) {
    console.error('获取会话列表失败', e)
  }
}

const selectSession = async (session) => {
  activeSessionId.value = session.id
  await fetchMessages()
}

const fetchMessages = async () => {
  if (!activeSessionId.value) return
  messagesLoading.value = true
  try {
    const res = await getChatMessages(activeSessionId.value)
    const rawList = res.data || []
    messages.value = rawList.map(msg => ({
      ...msg,
      isSelf: msg.senderId === currentUser?.userId,
      createTime: msg.sendTime,
    }))
    await nextTick()
    scrollToBottom()
  } catch (e) {
    ElMessage.error('获取消息失败')
  } finally {
    messagesLoading.value = false
  }
}

const handleNewSession = async () => {
  try {
    const res = await createChatSession()
    const newSession = res.data
    sessions.value.unshift(newSession)
    activeSessionId.value = newSession.id
    messages.value = []
  } catch (e) {
    ElMessage.error('创建会话失败')
  }
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text) return

  // 乐观添加消息到本地
  messages.value.push({
    id: Date.now(),
    content: text,
    isSelf: true,
    createTime: new Date().toLocaleString(),
  })
  inputText.value = ''
  await nextTick()
  scrollToBottom()

  // TODO: 通过 WebSocket 或 API 发送消息
}

const scrollToBottom = () => {
  if (messageAreaRef.value) {
    messageAreaRef.value.scrollTop = messageAreaRef.value.scrollHeight
  }
}

onMounted(() => {
  fetchSessions()
})
</script>

<style scoped>
.chat-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
  height: calc(100vh - 120px);
}
.chat-container {
  height: 100%;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}
.session-panel {
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  font-weight: bold;
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
  background: #f5f7fa;
}
.session-item.active {
  background: #ecf5ff;
}
.session-info {
  flex: 1;
  min-width: 0;
}
.session-name {
  font-size: 14px;
  font-weight: 500;
}
.session-last {
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.session-time {
  font-size: 11px;
  color: #c0c4cc;
  flex-shrink: 0;
}
.chat-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.chat-header {
  padding: 12px 20px;
  border-bottom: 1px solid #e4e7ed;
  font-weight: bold;
  font-size: 15px;
}
.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
}
.loading-tip {
  text-align: center;
  color: #999;
  padding: 16px;
}
.message-row {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}
.message-row.is-self {
  flex-direction: row-reverse;
}
.message-bubble {
  max-width: 60%;
}
.message-text {
  background: #f0f0f0;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}
.is-self .message-text {
  background: #409eff;
  color: #fff;
}
.message-time {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 4px;
}
.is-self .message-time {
  text-align: right;
}
.input-area {
  border-top: 1px solid #e4e7ed;
  padding: 12px 16px;
}
.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
.no-session {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
