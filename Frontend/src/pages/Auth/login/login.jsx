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
import { useEffect,useState } from "react";
import { useNavigate } from "react-router-dom";
import SpinnerBackdrop from "@/components/custom/SpinnerBackdrop";
import { GoogleLogin } from "@react-oauth/google";

const formSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(8, "Password must be at least 8 characters long"),
});

const LoginForm = () => {
  const navigate = useNavigate();
  const { login, loginWithGoogle, loading } = useAuth();
  const [loadingMessage, setLoadingMessage] = useState("");

  useEffect(() => {
    console.log("[LoginForm] mounted");
  }, []);

  const form = useForm({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const onSubmit = async (data) => {
    setLoadingMessage("Signing you in...");
    data.navigate = navigate;
    try {
      await login(data);
    } catch (error) {
      setLoadingMessage("");
    }
  };

  const handleGoogleLoginSuccess = async (credentialResponse) => {
    console.log("[LoginForm] google login success response received");
    setLoadingMessage("Verifying Google account...");
    
    // Switch message after 2 seconds to show progress
    setTimeout(() => {
      setLoadingMessage("Initializing your secure wallet...");
    }, 2000);

    try {
      await loginWithGoogle({ 
        idToken: credentialResponse.credential, 
        navigate 
      });
    } catch (error) {
      // Error handled in context
    } finally {
      setLoadingMessage("");
    }
  };

  return (
    <div className="space-y-6 w-full">
      <div className="text-center space-y-1">
        <h2 className="text-app-textPrimary font-semibold text-xl">Login</h2>
        <p className="text-app-textSecondary text-sm">Enter your credentials to access your account</p>
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
                    placeholder="Enter your email"
                  />
                </FormControl>
                <FormMessage className="text-app-error text-xs" />
              </FormItem>
            )}
          />
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
                    placeholder="Enter your password"
                  />
                </FormControl>
                <FormMessage className="text-app-error text-xs" />
              </FormItem>
            )}
          />

          <Button 
            disabled={loading}
            type="submit" 
            className="w-full bg-brand-primary hover:bg-brand-dark text-white 
            font-semibold py-2.5 rounded-input transition-colors duration-200"
          >
            {loading ? "Signing in..." : "Login"}
          </Button>
        </form>
      </Form>

      <div className="relative py-2">
        <div className="absolute inset-0 flex items-center">
          <span className="w-full border-t border-app-border" />
        </div>
        <div className="relative flex justify-center text-xs uppercase">
          <span className="bg-white dark:bg-[#1a1a2e] px-2 text-app-textSecondary font-medium">Or continue with</span>
        </div>
      </div>

      <div className="flex justify-center w-full">
        <GoogleLogin
          clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID}
          onSuccess={handleGoogleLoginSuccess}
          onError={() => {
            console.log("[LoginForm] Google Login Failed");
          }}
          useOneTap
          theme="outline"
          shape="pill"
          width="100%"
        />
      </div>

      <SpinnerBackdrop show={loading} message={loadingMessage} />
    </div>
  );
};

export default LoginForm;
