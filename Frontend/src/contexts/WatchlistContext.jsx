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

  const getUserWatchlist = async (forceRefresh = false) => {
    // If we already have items and not forcing refresh, return existing items
    if (!forceRefresh && state.items && state.items.length > 0 && typeof state.items[0] === 'object') {
      console.log("[WatchlistContext] Using cached items from state");
      return state.items;
    }

    dispatch({ type: 'WATCHLIST_REQUEST' })
    try {
      console.log("[WatchlistContext] Fetching watchlist from backend...");
      const response = await axiosInstance.get('/api/watchlist/user')
      
      // The backend returns a list of objects (coin details)
      const data = response.data || [];
      console.log("[WatchlistContext] Received items:", data);
      
      dispatch({ type: 'WATCHLIST_SUCCESS', payload: { items: data } })
      return data
    } catch (error) {
      console.error("[WatchlistContext] Error fetching watchlist:", error);
      dispatch({ type: 'WATCHLIST_FAILURE', payload: error.message })
      throw error
    }
  }

  const addToWatchlist = async (coinId) => {
    dispatch({ type: 'WATCHLIST_REQUEST' })
    try {
      await axiosInstance.patch(`/api/watchlist/add/coin/${coinId}`)
      
      // Immediately update local state to provide instant feedback
      // We toggle the coin - if it's there, remove it; if not, we'll need to fetch details
      const isAlreadyInWatchlist = state.items.some(item => 
        (typeof item === 'string' ? item === coinId : item.id === coinId)
      );

      if (isAlreadyInWatchlist) {
        // Optimization: Remove locally without a full refetch
        const updatedItems = state.items.filter(item => 
          (typeof item === 'string' ? item !== coinId : item.id !== coinId)
        );
        dispatch({ type: 'WATCHLIST_SUCCESS', payload: { items: updatedItems } })
        showToast.success("Removed from watchlist")
      } else {
        // It's a new add, we need full details for the watchlist page
        // A full refresh is better here to ensure we have the correct CoinGecko object
        const response = await axiosInstance.get('/api/watchlist/user')
        dispatch({ type: 'WATCHLIST_SUCCESS', payload: { items: response.data || [] } })
        showToast.success("Added to watchlist")
      }
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
