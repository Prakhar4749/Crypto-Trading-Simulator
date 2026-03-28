/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import { useAuth } from "@/contexts/AuthContext";
import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogClose,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSeparator,
  InputOTPSlot,
} from "@/components/ui/input-otp";
import { Mail, ShieldCheck, Send, BadgeCheck, ArrowLeft } from "lucide-react";
import { useNavigate, useLocation } from "react-router-dom";
import { useToast } from "@/components/ui/use-toast";

const AccountVarificationForm = () => {
  const [value, setValue] = useState("");
  const { user, sendVerificationOtp, verifyOtp, enableTwoStepAuthentication, jwt } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { toast } = useToast();

  const verificationType = location.state?.type || "ID_VERIFICATION";

  useEffect(() => {
    console.log("[AccountVarificationForm] component mounted", { 
      from: location.state?.from,
      verificationType 
    });
  }, [user]);

  const handleSendOtp = (type) => {
    console.log("[AccountVarificationForm] sending OTP", { type });
    sendVerificationOtp({
      verificationType: type,
      jwt: jwt || localStorage.getItem("jwt"),
    }).then(() => {
      toast({
        title: "OTP Sent",
        description: "A 6-digit verification code has been sent to your email.",
      });
    }).catch(err => {
      toast({
        title: "Error Sending OTP",
        description: err.message || "Failed to send OTP. Please try again.",
        variant: "destructive",
      });
    });
  };

  const handleVerifyOtp = async () => {
    console.log("[AccountVarificationForm] verifyOtp called", { otp: value, verificationType });
    try {
      if (verificationType === "2FA_SETUP") {
        await enableTwoStepAuthentication({
          otp: value,
          jwt: jwt || localStorage.getItem("jwt")
        });
      } else {
        await verifyOtp({ 
          otp: value,
          jwt: jwt || localStorage.getItem("jwt")
        });
      }
      
      console.log("[AccountVarificationForm] verification success");

      toast({
        title: verificationType === "2FA_SETUP" ? "2FA Enabled Successfully" : "Email Verified Successfully",
        description: verificationType === "2FA_SETUP" ? "Your account is now secured with 2FA." : "Your identity has been verified.",
      });

      const destination = location.state?.from || "/profile";
      console.log("[AccountVarificationForm] navigating to", { destination });
      navigate(destination, { replace: true });

    } catch (error) {
      console.log("[AccountVarificationForm] verification failed", error.message);
      toast({
        title: "Verification Failed",
        description: error.message || "Invalid OTP. Please try again.",
        variant: "destructive",
      });
    }
  };

  const onOtpChange = (val) => {
    setValue(val);
    console.log("[AccountVarificationForm] OTP input changed", { length: val?.length });
  };

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-md mx-auto space-y-6">
        <Button 
          variant="ghost" 
          onClick={() => navigate(-1)} 
          className="text-app-textSecondary hover:text-brand-primary p-0 h-auto mb-4"
        >
          <ArrowLeft className="w-4 h-4 mr-2" /> Back
        </Button>

        <div className="text-center space-y-2 mb-8">
          <h1 className="text-app-textPrimary font-bold text-2xl">
            {verificationType === "2FA_SETUP" ? "Setup 2-Step Verification" : "Identity Verification"}
          </h1>
          <p className="text-app-textSecondary text-sm">
            {verificationType === "2FA_SETUP" 
              ? "Protect your account by enabling two-factor authentication." 
              : "Verify your email address to complete your profile."}
          </p>
        </div>

        <div className="bg-white rounded-card shadow-card border border-app-border p-8 space-y-8">
          <div className="flex items-center gap-4 bg-app-bg p-4 rounded-input border border-app-border">
            <div className="bg-brand-light p-2.5 rounded-full">
              <Mail className="w-6 h-6 text-brand-primary" />
            </div>
            <div className="space-y-0.5">
              <p className="text-app-textSecondary text-[10px] font-bold uppercase tracking-wider">Email Address</p>
              <p className="text-app-textPrimary font-semibold">{user?.email}</p>
            </div>
          </div>

          <div className="space-y-6">
            <div className="flex flex-col items-center gap-6">
              <Button 
                onClick={() => handleSendOtp("EMAIL")}
                className="w-full bg-brand-light text-brand-primary hover:bg-brand-primary hover:text-white font-bold h-12 rounded-input transition-all flex items-center justify-center gap-2 border border-brand-primary/20"
              >
                <Send className="w-4 h-4" /> Send Verification Code
              </Button>

              <div className="space-y-4 w-full flex flex-col items-center">
                <p className="text-app-textSecondary text-xs font-medium">Enter the 6-digit code sent to your email</p>
                <InputOTP
                  value={value}
                  onChange={onOtpChange}
                  maxLength={6}
                  className="gap-2"
                >
                  <InputOTPGroup className="gap-2">
                    <InputOTPSlot index={0} className="w-12 h-12 border border-app-border rounded-input text-lg font-bold text-app-textPrimary focus:ring-2 focus:ring-brand-primary text-center" />
                    <InputOTPSlot index={1} className="w-12 h-12 border border-app-border rounded-input text-lg font-bold text-app-textPrimary focus:ring-2 focus:ring-brand-primary text-center" />
                    <InputOTPSlot index={2} className="w-12 h-12 border border-app-border rounded-input text-lg font-bold text-app-textPrimary focus:ring-2 focus:ring-brand-primary text-center" />
                  </InputOTPGroup>
                  <InputOTPSeparator className="text-app-border" />
                  <InputOTPGroup className="gap-2">
                    <InputOTPSlot index={3} className="w-12 h-12 border border-app-border rounded-input text-lg font-bold text-app-textPrimary focus:ring-2 focus:ring-brand-primary text-center" />
                    <InputOTPSlot index={4} className="w-12 h-12 border border-app-border rounded-input text-lg font-bold text-app-textPrimary focus:ring-2 focus:ring-brand-primary text-center" />
                    <InputOTPSlot index={5} className="w-12 h-12 border border-app-border rounded-input text-lg font-bold text-app-textPrimary focus:ring-2 focus:ring-brand-primary text-center" />
                  </InputOTPGroup>
                </InputOTP>
              </div>
            </div>

            <Button
              onClick={() => {
                console.log("[AccountVarificationForm] submit clicked");
                handleVerifyOtp();
              }}
              disabled={value.length !== 6}
              className="w-full bg-brand-primary hover:bg-brand-dark text-white font-bold h-14 rounded-input shadow-lg transition-all active:scale-[0.98] mt-4"
            >
              Verify & Complete
            </Button>
          </div>
        </div>

        {user?.verified && verificationType === "ID_VERIFICATION" && (
          <div className="flex items-center justify-center gap-2 text-brand-primary font-bold bg-brand-light/50 py-4 rounded-input border border-brand-primary/20">
            <BadgeCheck className="w-6 h-6" />
            <span>Identity Already Verified</span>
          </div>
        )}
      </div>
    </div>
  );
};

export default AccountVarificationForm;
