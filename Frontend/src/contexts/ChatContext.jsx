import React, { createContext, useContext, useReducer } from 'react'
import axiosInstance from '../config/axiosInstance'
import { showToast } from '@/utils/toast'

// 1. Initial State
const initialState = {
  message: null,
  messages: [],
  loading: false,
  error: null
}

// 2. Reducer
function chatReducer(state, action) {
  switch (action.type) {
    case 'CHAT_REQUEST':
      return { 
        ...state, 
        loading: true, 
        error: null,
        messages: action.payload ? [...state.messages, action.payload] : state.messages
      }
    case 'CHAT_SUCCESS':
      return { 
        ...state, 
        loading: false, 
        message: action.payload,
        messages: [...state.messages, action.payload]
      }
    case 'CHAT_FAILURE':
      return { ...state, loading: false, error: action.payload }
    case 'CLEAR_CHAT':
      return initialState
    case 'SET_MESSAGES':
      return { ...state, messages: action.payload, loading: false }
    default:
      return state
  }
}

// 3. Create Context
const ChatContext = createContext()

// 4. Provider Component
export const ChatProvider = ({ children }) => {
  const [state, dispatch] = useReducer(chatReducer, initialState)

  const sendMessage = async (prompt) => {
    dispatch({ type: 'CHAT_REQUEST', payload: { role: 'user', prompt } })
    try {
      const response = await axiosInstance.post("/api/chat/bot", { message: prompt })
      const aiReply = response.data
      
      const botMessage = { role: 'model', ans: aiReply }
      dispatch({ type: 'CHAT_SUCCESS', payload: botMessage })
      return botMessage
    } catch (error) {
      dispatch({ type: 'CHAT_FAILURE', payload: error.message })
      showToast.fromError(error)
      throw error
    }
  }

  const getMessages = async () => {
    dispatch({ type: 'CHAT_REQUEST' })
    try {
      const response = await axiosInstance.get('/api/chat/bot/history')
      const messages = response.data
      dispatch({ type: 'SET_MESSAGES', payload: messages })
      return messages
    } catch (error) {
      dispatch({ type: 'CHAT_FAILURE', payload: error.message })
      return []
    }
  }

  const clearChat = () => {
    dispatch({ type: 'CLEAR_CHAT' })
  }

  return (
    <ChatContext.Provider value={{ ...state, sendMessage, getMessages, clearChat }}>
      {children}
    </ChatContext.Provider>
  )
}

// 5. Custom Hook
export const useChat = () => {
  const context = useContext(ChatContext)
  if (!context) throw new Error("useChat must be used within ChatProvider")
  return context
}

export default ChatContext
