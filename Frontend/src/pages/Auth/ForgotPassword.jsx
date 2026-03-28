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
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { showToast } from "@/utils/toast";

const formSchema = z.object({
  email: z.string().email("Invalid email address"),
});

const ForgotPasswordForm = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { sendResetPasswordOtp } = useAuth();

  const form = useForm({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
    },
  });

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      await sendResetPasswordOtp({ 
        email: data.email, 
        navigate 
      });
    } catch (error) {
      showToast.fromError(error);
    } finally {
      setLoading(false);
    }
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
                    disabled={loading}
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
            disabled={loading}
            className="w-full bg-brand-primary hover:bg-brand-dark text-white 
            font-semibold py-2.5 rounded-input transition-colors duration-200"
          >
            {loading ? "Sending..." : "Send OTP"}
          </Button>
        </form>
      </Form>
    </div>
  );
};

export default ForgotPasswordForm;
