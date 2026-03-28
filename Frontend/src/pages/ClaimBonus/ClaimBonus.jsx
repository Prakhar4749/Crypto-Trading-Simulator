import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import axiosInstance from '../../config/axiosInstance';

export default function ClaimBonus() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState('loading'); // loading|success|error|already_claimed
  const [wallet, setWallet] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [countdown, setCountdown] = useState(5);

  const token = searchParams.get('token');

  useEffect(() => {
    if (!token) {
      setStatus('error');
      setErrorMessage('Invalid claim link. Please check your email.');
      return;
    }
    claimBonusToken(token);
  }, [token]);

  // Countdown redirect after success
  useEffect(() => {
    if (status === 'success') {
      const timer = setInterval(() => {
        setCountdown(prev => {
          if (prev <= 1) {
            clearInterval(timer);
            navigate('/');
          }
          return prev - 1;
        });
      }, 1000);
      return () => clearInterval(timer);
    }
  }, [status, navigate]);

  const claimBonusToken = async (token) => {
    try {
      const res = await axiosInstance.post(`/auth/claim-bonus?token=${token}`);
      setWallet(res.data);
      setStatus('success');
    } catch (error) {
      const msg = error?.message || 'Something went wrong';
      if (msg.toLowerCase().includes('already')) {
        setStatus('already_claimed');
      } else {
        setStatus('error');
      }
      setErrorMessage(msg);
    }
  };

  // ═══ RENDER ═══
  return (
    <div className="min-h-screen bg-[#0f0f1a] flex items-center justify-center p-4">
      
      {/* Logo Header */}
      <div className="absolute top-8 left-1/2 -translate-x-1/2">
        <img 
          src="/CoinDesk-logo.png" 
          alt="CoinDesk" 
          className="h-10 w-auto"
        />
      </div>

      <div className="bg-[#1a1a2e] rounded-2xl p-8 max-w-md w-full text-center border border-white/10 shadow-2xl mt-16">

        {/* LOADING STATE */}
        {status === 'loading' && (
          <div>
            <div className="w-16 h-16 border-4 border-[#00B386] border-t-transparent rounded-full animate-spin mx-auto mb-6" />
            <h2 className="text-white text-xl font-bold mb-2">
              Verifying your bonus...
            </h2>
            <p className="text-gray-400 text-sm">
              Please wait a moment
            </p>
          </div>
        )}

        {/* SUCCESS STATE */}
        {status === 'success' && (
          <div>
            <div className="text-6xl mb-4">🎉</div>
            <h2 className="text-white text-2xl font-bold mb-2">
              Bonus Claimed!
            </h2>
            <p className="text-gray-400 text-sm mb-6">
              Your welcome bonus has been added to your wallet
            </p>
            <div className="bg-[#00B386]/10 border border-[#00B386]/30 rounded-xl p-4 mb-6">
              <p className="text-gray-400 text-xs mb-1 uppercase tracking-wider">
                Bonus Credited
              </p>
              <p className="text-[#00B386] text-3xl font-bold">
                ${Number(import.meta.env.VITE_SIGNUP_BONUS || 10000).toLocaleString()}
              </p>
              <p className="text-gray-400 text-xs mt-1">
                Virtual USD
              </p>
            </div>
            <p className="text-gray-500 text-xs mb-4">
              Redirecting to dashboard in {countdown}s...
            </p>
            <button
              onClick={() => navigate('/')}
              className="w-full bg-[#00B386] text-white py-3 rounded-xl font-semibold hover:bg-[#009970] transition-colors"
            >
              Go to Dashboard Now →
            </button>
          </div>
        )}

        {/* ALREADY CLAIMED STATE */}
        {status === 'already_claimed' && (
          <div>
            <div className="text-6xl mb-4">ℹ️</div>
            <h2 className="text-white text-2xl font-bold mb-2">
              Already Claimed
            </h2>
            <p className="text-gray-400 text-sm mb-6">
              This bonus has already been claimed for your account.
            </p>
            <button
              onClick={() => navigate('/')}
              className="w-full bg-[#00B386] text-white py-3 rounded-xl font-semibold hover:bg-[#009970] transition-colors"
            >
              Go to Dashboard
            </button>
          </div>
        )}

        {/* ERROR STATE */}
        {status === 'error' && (
          <div>
            <div className="text-6xl mb-4">❌</div>
            <h2 className="text-white text-2xl font-bold mb-2">
              Claim Failed
            </h2>
            <p className="text-gray-400 text-sm mb-2">
              {errorMessage}
            </p>
            <p className="text-gray-500 text-xs mb-6">
              The link may have expired (valid for 24 hours). Contact support if this persists.
            </p>
            <button
              onClick={() => navigate('/')}
              className="w-full bg-[#1a1a3e] border border-white/10 text-white py-3 rounded-xl font-semibold hover:bg-white/5 transition-colors"
            >
              Go to Dashboard
            </button>
          </div>
        )}

      </div>
    </div>
  );
}
