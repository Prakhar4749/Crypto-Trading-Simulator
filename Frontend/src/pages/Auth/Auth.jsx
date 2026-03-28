/* eslint-disable no-unused-vars */
import { Button } from "@/components/ui/button";
import SignupForm from "./signup/SignupForm";
import LoginForm from "./login/login";
import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import ForgotPasswordForm from "./ForgotPassword";
import { useAuth } from "../../contexts/AuthContext";

const Auth = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();

  useEffect(() => {
    console.log("[Auth] mounted");
  }, []);

  const handleNavigation = (path) => {
    navigate(path);
  };

  return (
    <div className="min-h-screen bg-app-bg flex items-center justify-center p-4">
      <div className="bg-white rounded-card shadow-card p-8 w-full max-w-md border border-app-border flex flex-col items-center">
        <img 
          src={import.meta.env.VITE_LOGO_URL || "/CoinDesk-logo.png"} 
          alt={import.meta.env.VITE_APP_NAME || "CoinDesk"} 
          className="h-10 w-auto cursor-pointer mb-2"
          onClick={() => navigate("/")}
        />
        
        {location.pathname === "/signup" ? (
          <div className="w-full space-y-6">
            <SignupForm />
            <div className="flex flex-col items-center gap-2 pt-2">
              <p className="text-app-textSecondary text-sm">Already have an account?</p>
              <Button
                onClick={() => handleNavigation("/signin")}
                variant="ghost"
                className="text-brand-primary hover:text-brand-dark font-medium p-0 h-auto"
              >
                Sign in instead
              </Button>
            </div>
          </div>
        ) : location.pathname === "/forgot-password" ? (
          <div className="w-full space-y-6">
            <ForgotPasswordForm />
            <div className="flex flex-col items-center gap-2 pt-2">
              <Button 
                onClick={() => navigate("/signin")} 
                variant="ghost"
                className="text-brand-primary hover:text-brand-dark font-medium p-0 h-auto"
              >
                Back to Login
              </Button>
            </div>
          </div>
        ) : (
          <div className="w-full space-y-6">
            <LoginForm />
            <div className="flex flex-col items-center gap-4 pt-2">
              <div className="flex flex-col items-center gap-1">
                <p className="text-app-textSecondary text-sm">Don't have an account?</p>
                <Button
                  onClick={() => handleNavigation("/signup")}
                  variant="ghost"
                  className="text-brand-primary hover:text-brand-dark font-medium p-0 h-auto"
                >
                  Create new account
                </Button>
              </div>
              
              <Button
                onClick={() => navigate("/forgot-password")}
                variant="link"
                className="text-app-textSecondary hover:text-brand-primary text-xs p-0 h-auto"
              >
                Forgot your password?
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Auth;
