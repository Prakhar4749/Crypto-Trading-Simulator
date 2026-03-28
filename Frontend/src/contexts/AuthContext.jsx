import React, { createContext, useContext, useReducer } from 'react'
import axiosInstance from '../config/axiosInstance'
import { showToast } from '@/utils/toast'

// 1. Initial State
const initialState = {
  user: null,
  jwt: localStorage.getItem("jwt") || null,
  loading: false,
  error: null,
  twoFactorAuth: null,
  kycStatus: null
}

// 2. Reducer
function authReducer(state, action) {
  switch (action.type) {
    case 'AUTH_REQUEST':
      return { ...state, loading: true, error: null }
    case 'AUTH_SUCCESS':
      return { ...state, loading: false, ...action.payload }
    case 'AUTH_FAILURE':
      return { ...state, loading: false, error: action.payload }
    case 'LOGOUT':
      return { ...initialState, jwt: null }
    case 'SET_USER':
      return { ...state, user: action.payload, loading: false }
    case 'SET_LOADING':
      return { ...state, loading: action.payload }
    case 'SET_ERROR':
      return { ...state, error: action.payload, loading: false }
    case 'KYC_SUCCESS':
      return { ...state, kycStatus: action.payload, loading: false }
    default:
      return state
  }
}

// 3. Create Context
const AuthContext = createContext()

// 4. Provider Component
export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState)

  const login = async (credentials) => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.post(`/auth/signin`, credentials)
      const data = response.data
      
      if (data.twoFactorAuthEnabled) {
        dispatch({ type: 'AUTH_SUCCESS', payload: { twoFactorAuth: data.session } })
        showToast.info("Two-Factor Authentication required")
        if (credentials.navigate) {
          credentials.navigate(`/two-factor-auth/${data.session}`)
        }
      } else if (data.jwt) {
        localStorage.setItem("jwt", data.jwt)
        if (data.user?.role) {
          localStorage.setItem("userRole", data.user.role)
        }
        dispatch({ type: 'AUTH_SUCCESS', payload: { jwt: data.jwt, user: data.user } })
        showToast.success("Login successful!")
        if (credentials.navigate) {
          credentials.navigate("/")
        }
      }
      return data
    } catch (err) {
      dispatch({ type: 'AUTH_FAILURE', payload: err.message })
      showToast.fromError(err)
      throw err
    }
  }

  const loginWithGoogle = async ({ idToken, navigate }) => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.post(`/auth/google`, { idToken })
      const data = response.data
      if (data.jwt) {
        localStorage.setItem("jwt", data.jwt)
        if (data.user?.role) {
          localStorage.setItem("userRole", data.user.role)
        }
        dispatch({ type: 'AUTH_SUCCESS', payload: { jwt: data.jwt, user: data.user } })
        showToast.success("Google login successful!")
        if (navigate) {
          navigate("/")
        }
      }
      return data
    } catch (err) {
      dispatch({ type: 'AUTH_FAILURE', payload: err.message })
      showToast.fromError(err)
      throw err
    }
  }

  const register = async (userData) => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.post(`/auth/signup`, userData)
      const data = response.data
      if (data.jwt) {
        localStorage.setItem("jwt", data.jwt)
        if (data.user?.role) {
          localStorage.setItem("userRole", data.user.role)
        }
      }
      dispatch({ type: 'AUTH_SUCCESS', payload: { jwt: data.jwt, user: data.user } })
      showToast.success("Registration successful!")
      if (userData.navigate) {
        userData.navigate("/")
      }
      return data
    } catch (err) {
      dispatch({ type: 'AUTH_FAILURE', payload: err.message })
      showToast.fromError(err)
      throw err
    }
  }

  const logout = () => {
    localStorage.removeItem("jwt")
    localStorage.removeItem("userRole")
    localStorage.removeItem("user")
    dispatch({ type: 'LOGOUT' })
    showToast.info("Logged out successfully")
  }

  const getUser = async () => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/users/profile`)
      const user = response.data
      if (user?.role) {
        localStorage.setItem("userRole", user.role)
      }
      dispatch({ type: 'SET_USER', payload: user })
      return user
    } catch (err) {
      dispatch({ type: 'AUTH_FAILURE', payload: err.message })
      // Don't show toast for every background profile fetch failure
      throw err
    }
  }

  const claimSignupBonus = async (token) => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.post(`/auth/claim-bonus`, null, {
        params: { token }
      })
      dispatch({ type: 'AUTH_SUCCESS', payload: {} })
      showToast.success("Sign-up bonus claimed successfully!")
      return response.data
    } catch (err) {
      showToast.fromError(err)
      throw err
    }
  }

  const updateUserProfile = async (userData) => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.patch(`/api/users/profile/update`, userData)
      dispatch({ type: 'SET_USER', payload: response.data })
      showToast.success("Profile updated successfully")
      return response.data
    } catch (err) {
      showToast.fromError(err)
      throw err
    }
  }

  const getKycStatus = async () => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.get(`/api/users/kyc/status`)
      dispatch({ type: 'KYC_SUCCESS', payload: response.data })
      return response.data
    } catch (err) {
      showToast.fromError(err)
      throw err
    }
  }

  const sendVerificationOtp = async (type) => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.post(`/auth/users/verification/${type}/send-otp`)
      dispatch({ type: 'AUTH_SUCCESS', payload: {} })
      showToast.success("OTP sent successfully")
      return response.data
    } catch (err) {
      showToast.fromError(err)
      throw err
    }
  }

  const verifyOtp = async ({ otp, session }) => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      let response
      if (session) {
        // 2FA login verification
        response = await axiosInstance.post(
          `/auth/two-factor/otp/${otp}`,
          { id: session }
        )
      } else {
        // Email verification for logged-in user
        response = await axiosInstance.patch(`/auth/users/verify-email/verify-otp/${otp}`)
      }
      
      const data = response.data
      if (data.jwt) {
        localStorage.setItem("jwt", data.jwt)
        if (data.user?.role) {
          localStorage.setItem("userRole", data.user.role)
        }
      }
      dispatch({ type: 'AUTH_SUCCESS', payload: { jwt: data.jwt, user: data.user } })
      showToast.success("Verification successful")
      return data
    } catch (err) {
      showToast.fromError(err)
      throw err
    }
  }

  const enableTwoStepAuthentication = async () => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.patch(`/api/users/enable-two-factor`)
      getUser()
      dispatch({ type: 'AUTH_SUCCESS', payload: {} })
      showToast.success("2FA settings updated")
      return response.data
    } catch (err) {
      showToast.fromError(err)
      throw err
    }
  }

  const sendResetPasswordOtp = async ({ email, navigate }) => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.post(`/auth/users/reset-password/send-otp`, {
        email
      })
      dispatch({ type: 'AUTH_SUCCESS', payload: {} })
      showToast.success("Password reset OTP sent")
      if (navigate) {
        navigate(`/reset-password?email=${encodeURIComponent(email)}`)
      }
      return response.data
    } catch (err) {
      showToast.fromError(err)
      throw err
    }
  }

  const verifyResetPasswordOtp = async ({ email, otp, password, navigate }) => {
    dispatch({ type: 'AUTH_REQUEST' })
    try {
      const response = await axiosInstance.patch(`/auth/users/reset-password/verify-otp`, {
        email,
        otp,
        newPassword: password
      })
      dispatch({ type: 'AUTH_SUCCESS', payload: {} })
      showToast.success("Password updated successfully!")
      if (navigate) {
        navigate("/password-update-successfully")
      }
      return response.data
    } catch (err) {
      showToast.fromError(err)
      throw err
    }
  }

  return (
    <AuthContext.Provider value={{ 
      ...state, 
      login, 
      loginWithGoogle,
      logout, 
      register, 
      getUser, 
      claimSignupBonus,
      updateUserProfile,
      getKycStatus,
      sendVerificationOtp, 
      verifyOtp, 
      enableTwoStepAuthentication, 
      sendResetPasswordOtp, 
      verifyResetPasswordOtp 
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) throw new Error("useAuth must be used within AuthProvider")
  return context
}

export default AuthContext
