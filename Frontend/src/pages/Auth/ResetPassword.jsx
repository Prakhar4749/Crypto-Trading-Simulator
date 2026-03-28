import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { useAuth } from "@/contexts/AuthContext";
import { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSeparator,
  InputOTPSlot,
} from "@/components/ui/input-otp";
import * as yup from "yup";

const formSchema = yup.object({
  password: yup
    .string()
    .min(8, "Password must be at least 8 characters long")
    .required("Password is required"),
  confirmPassword: yup
    .string()
    .oneOf([yup.ref('password')], "Passwords & Confirm Password must match")
    .min(8, "Password must be at least 8 characters long")
    .required("Confirm password is required"),
  otp: yup
    .string()
    .min(6, "OTP must be at least 6 characters long")
    .required("OTP is required"),
});

const ResetPasswordForm = () => {
  const navigate = useNavigate();
  const { verifyResetPassowrdOTP } = useAuth();
  const { session } = useParams();

  useEffect(() => {
    console.log("[ResetPasswordForm] mounted");
  }, []);

  const form = useForm({
    resolver: yupResolver(formSchema),
    defaultValues: {
      confirmPassword: "",
      password: "",
      otp: "",
    },
  });

  const onSubmit = (data) => {
    verifyResetPassowrdOTP({ otp: data.otp, password: data.password, session, navigate });
    console.log("reset password form", data);
  };

  return (
    <div className="min-h-screen bg-app-bg flex items-center justify-center p-4">
      <div className="bg-white rounded-card shadow-card p-8 w-full max-w-md border border-app-border">
        <div className="space-y-6">
          <div className="text-center space-y-1">
            {/* TODO: Replace with official CoinDesk logo */}
            <img 
              src="https://via.placeholder.com/150?text=CoinDesk+Logo" 
              alt="CoinDesk Logo" 
              className="h-8 w-auto mx-auto mb-2 cursor-pointer"
              onClick={() => navigate("/")}
            />
            <h2 className="text-app-textPrimary font-semibold text-xl">Reset Password</h2>
            <p className="text-app-textSecondary text-sm">Enter the code and your new password</p>
          </div>

          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              <FormField
                control={form.control}
                name="otp"
                render={({ field }) => (
                  <FormItem className="flex flex-col items-center">
                    <p className="text-app-textPrimary font-medium text-sm mb-2 self-start">Verification Code</p>
                    <FormControl>
                      <InputOTP {...field} maxLength={6} className="gap-2">
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
                    </FormControl>
                    <FormMessage className="text-app-error text-xs" />
                  </FormItem>
                )}
              />

              <div className="space-y-4">
                <FormField
                  control={form.control}
                  name="password"
                  render={({ field }) => (
                    <FormItem>
                      <FormControl>
                        <Input
                          {...field}
                          type="password"
                          className="w-full border border-app-border rounded-input px-4 py-2.5 
                          focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-brand-primary
                          text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                          placeholder="New Password"
                        />
                      </FormControl>
                      <FormMessage className="text-app-error text-xs" />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="confirmPassword"
                  render={({ field }) => (
                    <FormItem>
                      <FormControl>
                        <Input
                          {...field}
                          type="password"
                          className="w-full border border-app-border rounded-input px-4 py-2.5 
                          focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-brand-primary
                          text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                          placeholder="Confirm New Password"
                        />
                      </FormControl>
                      <FormMessage className="text-app-error text-xs" />
                    </FormItem>
                  )}
                />
              </div>

              <Button 
                type="submit" 
                className="w-full bg-brand-primary hover:bg-brand-dark text-white 
                font-semibold py-2.5 rounded-input transition-colors duration-200"
              >
                Change Password
              </Button>
            </form>
          </Form>

          <div className="flex justify-center pt-2">
            <Button
              onClick={() => navigate("/signin")}
              variant="ghost"
              className="text-brand-primary hover:text-brand-dark font-medium text-sm"
            >
              Back to Login
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResetPasswordForm;
