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
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useAuth } from "@/contexts/AuthContext";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const formSchema = z.object({
  email: z.string().email("Invalid email address"),
});

const ForgotPasswordForm = () => {
  const [verificationType, setVerificationType] = useState("EMAIL");
  const navigate = useNavigate();
  const { sendResetPassowrdOTP } = useAuth();

  useEffect(() => {
    console.log("[ForgotPasswordForm] mounted");
  }, []);

  const form = useForm({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
    },
  });

  const onSubmit = (data) => {
    data.navigate = navigate;
    sendResetPassowrdOTP({ 
      sendTo: data.email, 
      navigate, 
      verificationType 
    });
    console.log("forgot password form", data);
  };

  return (
    <div className="space-y-6 w-full">
      <div className="text-center space-y-1">
        <h2 className="text-app-textPrimary font-semibold text-xl">Forgot Password</h2>
        <p className="text-app-textSecondary text-sm">Enter your email to receive a reset code</p>
      </div>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <FormField
            control={form.control}
            name="email"
            render={({ field }) => (
              <FormItem>
                <FormControl>
                  <Input
                    {...field}
                    className="w-full border border-app-border rounded-input px-4 py-2.5 
                    focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-brand-primary
                    text-app-textPrimary placeholder:text-app-textSecondary bg-transparent"
                    placeholder="Enter your email address"
                  />
                </FormControl>
                <FormMessage className="text-app-error text-xs" />
              </FormItem>
            )}
          />

          <Button 
            type="submit" 
            className="w-full bg-brand-primary hover:bg-brand-dark text-white 
            font-semibold py-2.5 rounded-input transition-colors duration-200"
          >
            Send OTP
          </Button>
        </form>
      </Form>
    </div>
  );
};

export default ForgotPasswordForm;
