import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";

const Notfound = () => {
  const navigate = useNavigate();
  return (
    <div className="bg-app-bg min-h-screen flex flex-col items-center justify-center text-center p-6">
      <p className="text-brand-primary font-black text-[120px] leading-none">404</p>
      <h1 className="text-app-textPrimary font-bold text-2xl mt-4">Page Not Found</h1>
      <p className="text-app-textSecondary text-sm mt-2 mb-8 max-w-xs">
        The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.
      </p>
      <Button 
        onClick={() => navigate("/")}
        className="bg-brand-primary hover:bg-brand-dark text-white font-bold py-6 px-8 rounded-input shadow-lg transition-all active:scale-[0.98]"
      >
        Back to Dashboard
      </Button>
    </div>
  );
};

export default Notfound;
