import { Button } from '@/components/ui/button'
import { CheckCircle2 } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { useWallet } from '@/contexts/WalletContext'
import { useAuth } from '@/contexts/AuthContext'
import { useEffect } from 'react'

const PaymentSuccess = () => {
  const navigate = useNavigate();
  const { userWallet, getUserWallet } = useWallet();
  const { jwt } = useAuth();

  useEffect(() => {
    const currentJwt = jwt || localStorage.getItem("jwt");
    if (currentJwt) {
      getUserWallet(currentJwt);
    }
  }, []);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-app-bg p-6">
      <div className="flex flex-col items-center max-w-sm text-center">
        <CheckCircle2 className="text-brand-primary w-20 h-20 mb-6" />
        <h1 className="text-app-textPrimary font-bold text-3xl mb-2">Payment Successful</h1>
        <p className="text-app-textSecondary text-sm mb-8">
          Your wallet has been topped up successfully. Your new balance is now available for trading.
        </p>
        
        <div className="bg-white rounded-card shadow-card border border-app-border p-6 w-full mb-8">
          <p className="text-app-textSecondary text-xs font-semibold uppercase tracking-wider mb-1">Current Balance</p>
          <p className="text-app-textPrimary font-bold text-3xl">
            ${userWallet?.balance?.toLocaleString() || "0.00"}
          </p>
        </div>

        <Button 
          onClick={() => navigate("/wallet")}
          className="w-full bg-brand-primary hover:bg-brand-dark text-white font-bold py-6 rounded-input shadow-lg transition-all active:scale-[0.98]"
        >
          Back to Wallet
        </Button>
      </div>
    </div>
  )
}

export default PaymentSuccess
