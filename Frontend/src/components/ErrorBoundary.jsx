import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('[ErrorBoundary]', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-[#0f0f1a] flex items-center justify-center p-4">
          <div className="bg-[#1a1a2e] rounded-xl p-8 max-w-md w-full text-center border border-red-500/30">
            <div className="text-6xl mb-4">
              ⚠️
            </div>
            <h2 className="text-white text-2xl font-bold mb-2">
              Something went wrong
            </h2>
            <p className="text-gray-400 mb-6 text-sm">
              {this.state.error?.message || 'An unexpected error occurred'}
            </p>
            <button
              onClick={() => window.location.reload()}
              className="bg-[#00B386] text-white px-6 py-2 rounded-lg hover:bg-[#009970] transition-colors"
            >
              Reload Page
            </button>
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}

export default ErrorBoundary;
