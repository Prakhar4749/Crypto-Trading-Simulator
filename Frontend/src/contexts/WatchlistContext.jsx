import React, { createContext, useContext, useReducer } from 'react'
import axiosInstance from '../config/axiosInstance'
import { showToast } from '@/utils/toast'

// 1. Initial State
const initialState = {
  watchlist: null,
  items: [],
  loading: false,
  error: null
}

// 2. Reducer
function watchlistReducer(state, action) {
  switch (action.type) {
    case 'WATCHLIST_REQUEST':
      return { ...state, loading: true, error: null }
    case 'WATCHLIST_SUCCESS':
      return { ...state, loading: false, ...action.payload }
    case 'WATCHLIST_FAILURE':
      return { ...state, loading: false, error: action.payload }
    default:
      return state
  }
}

// 3. Create Context
const WatchlistContext = createContext()

// 4. Provider Component
export const WatchlistProvider = ({ children }) => {
  const [state, dispatch] = useReducer(watchlistReducer, initialState)

  const getUserWatchlist = async () => {
    dispatch({ type: 'WATCHLIST_REQUEST' })
    try {
      const response = await axiosInstance.get('/api/watchlist/user')
      dispatch({ type: 'WATCHLIST_SUCCESS', payload: { watchlist: response.data, items: response.data.items || [] } })
      return response.data
    } catch (error) {
      dispatch({ type: 'WATCHLIST_FAILURE', payload: error.message })
      throw error
    }
  }

  const addToWatchlist = async (coinId) => {
    dispatch({ type: 'WATCHLIST_REQUEST' })
    try {
      const response = await axiosInstance.patch(`/api/watchlist/add/coin/${coinId}`)
      dispatch({ type: 'WATCHLIST_SUCCESS', payload: { watchlist: response.data, items: response.data.items || [] } })
      showToast.success("Watchlist updated")
      return response.data
    } catch (error) {
      dispatch({ type: 'WATCHLIST_FAILURE', payload: error.message })
      showToast.fromError(error)
      throw error
    }
  }

  return (
    <WatchlistContext.Provider value={{ ...state, getUserWatchlist, addToWatchlist }}>
      {children}
    </WatchlistContext.Provider>
  )
}

// 5. Custom Hook
export const useWatchlist = () => {
  const context = useContext(WatchlistContext)
  if (!context) throw new Error("useWatchlist must be used within WatchlistProvider")
  return context
}

export default WatchlistContext
