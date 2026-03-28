import { Button } from "@/components/ui/button";
import { CheckCircle2 } from "lucide-react";
import { useNavigate } from "react-router-dom";

const PasswordUpdateSuccess = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-app-bg flex items-center justify-center p-4">
      <div className="bg-white rounded-card shadow-card p-10 w-full max-w-md border border-app-border flex flex-col items-center text-center">
        <div className="w-20 h-20 bg-brand-light rounded-full flex items-center justify-center mb-6">
          <CheckCircle2 className="w-10 h-10 text-brand-primary" />
        </div>

        <img 
          src={import.meta.env.VITE_LOGO_URL || "/CoinDesk-logo.png"} 
          alt={import.meta.env.VITE_APP_NAME || "CoinDesk"} 
          className="h-10 w-auto mb-2 cursor-pointer"
          onClick={() => navigate("/")}
        />
        <h2 className="text-app-textPrimary font-semibold text-xl mb-2">Password Updated!</h2>
        <p className="text-app-textSecondary text-sm mb-8 px-4">
          Your password has been successfully reset. You can now use your new password to log in to your account.
        </p>

        <Button
          onClick={() => navigate("/signin")}
          className="w-full bg-brand-primary hover:bg-brand-dark text-white 
          font-semibold py-6 rounded-input transition-colors duration-200"
        >
          Go to Sign In
        </Button>
      </div>
    </div>
  );
};

export default PasswordUpdateSuccess;
