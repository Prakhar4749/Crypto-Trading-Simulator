import { Button } from "@/components/ui/button";
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSeparator,
  InputOTPSlot,
} from "@/components/ui/input-otp";
import { useState } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { useNavigate, useParams } from "react-router-dom";
import { ShieldCheck } from "lucide-react";
import { showToast } from "@/utils/toast";

const TwoFactorAuth = () => {
  const [value, setValue] = useState("");
  const [loading, setLoading] = useState(false);
  const { verifyOtp } = useAuth();
  const { session } = useParams();
  const navigate = useNavigate();

  const handleTwoFactoreAuth = async () => { 
    if (value.length !== 6) {
      showToast.error("Please enter a 6-digit code");
      return;
    }

    setLoading(true);
    try {
      const data = await verifyOtp({ otp: value, session });
      if (data.jwt) {
        navigate("/");
      }
    } catch (err) {
      showToast.fromError(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-app-bg flex items-center justify-center p-4">
      <div className="bg-white rounded-card shadow-card p-8 w-full max-w-md border border-app-border flex flex-col items-center">
        <div className="w-16 h-16 bg-brand-light rounded-full flex items-center justify-center mb-6">
          <ShieldCheck className="w-8 h-8 text-brand-primary" />
        </div>
        
        <div className="text-center space-y-1 mb-8">
          <img 
            src={import.meta.env.VITE_LOGO_URL || "/CoinDesk-logo.png"} 
            alt={import.meta.env.VITE_APP_NAME || "CoinDesk"} 
            className="h-8 w-auto mx-auto mb-2 cursor-pointer"
            onClick={() => navigate("/")}
          />
          <h2 className="text-app-textPrimary font-semibold text-xl">2-Step Verification</h2>
          <p className="text-app-textSecondary text-sm">Please enter the code sent to your email</p>
        </div>

        <div className="space-y-8 w-full flex flex-col items-center">
          <div className="space-y-4 flex flex-col items-center">
            <InputOTP
              value={value}
              onChange={(value) => setValue(value)}
              maxLength={6}
            >
              <InputOTPGroup>
                <InputOTPSlot index={0} className="border-app-border" />
                <InputOTPSlot index={1} className="border-app-border" />
                <InputOTPSlot index={2} className="border-app-border" />
              </InputOTPGroup>
              <InputOTPSeparator />
              <InputOTPGroup>
                <InputOTPSlot index={3} className="border-app-border" />
                <InputOTPSlot index={4} className="border-app-border" />
                <InputOTPSlot index={5} className="border-app-border" />
              </InputOTPGroup>
            </InputOTP>
            <p className="text-app-textSecondary text-xs">The code is valid for 10 minutes</p>
          </div>
          
          <Button 
            onClick={handleTwoFactoreAuth} 
            className="w-full bg-brand-primary hover:bg-brand-dark text-white 
            font-semibold py-2.5 rounded-input transition-colors duration-200"
          >
            Verify & Proceed
          </Button>
          
          <Button 
            onClick={() => navigate("/signin")}
            variant="ghost"
            className="text-app-textSecondary hover:text-brand-primary text-sm p-0 h-auto"
          >
            Back to Sign In
          </Button>
        </div>
      </div>
    </div>
  );
};

export default TwoFactorAuth;
