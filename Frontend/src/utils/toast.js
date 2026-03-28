import { toast } from "@/components/ui/use-toast";

// Design: dark background, colored left border, icon + message
const toastStyles = {
  success: {
    style: {
      background: '#1a1a2e',
      border: '1px solid #00B386',
      borderLeft: '4px solid #00B386',
      color: '#ffffff',
      borderRadius: '8px',
      padding: '12px 16px',
    }
  },
  error: {
    style: {
      background: '#1a1a2e',
      border: '1px solid #ef4444',
      borderLeft: '4px solid #ef4444',
      color: '#ffffff',
      borderRadius: '8px',
      padding: '12px 16px',
    }
  },
  warning: {
    style: {
      background: '#1a1a2e',
      border: '1px solid #f59e0b',
      borderLeft: '4px solid #f59e0b',
      color: '#ffffff',
      borderRadius: '8px',
      padding: '12px 16px',
    }
  },
  info: {
    style: {
      background: '#1a1a2e',
      border: '1px solid #3b82f6',
      borderLeft: '4px solid #3b82f6',
      color: '#ffffff',
      borderRadius: '8px',
      padding: '12px 16px',
    }
  }
};

export const showToast = {
  success: (message, description) => 
    toast({
      title: '✅ ' + message,
      description: description || '',
      ...toastStyles.success,
      duration: 4000,
    }),

  error: (message, details) => {
    // Show validation details if array
    const desc = Array.isArray(details) 
      ? details.join('\n') 
      : details || '';
    toast({
      title: '❌ ' + message,
      description: desc,
      ...toastStyles.error,
      duration: 6000,
    });
  },

  warning: (message, description) =>
    toast({
      title: '⚠️ ' + message,
      description: description || '',
      ...toastStyles.warning,
      duration: 5000,
    }),

  info: (message, description) =>
    toast({
      title: 'ℹ️ ' + message,
      description: description || '',
      ...toastStyles.info,
      duration: 4000,
    }),

  // Handle backend error object directly
  fromError: (error) => {
    const message = error?.message || 'Something went wrong';
    const details = error?.details || [];
    showToast.error(message, details);
  }
};

export default showToast;
