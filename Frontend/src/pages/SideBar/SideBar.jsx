import { Button } from "@/components/ui/button";
import { SheetClose } from "@/components/ui/sheet";
import {
  ExitIcon,
  BookmarkIcon,
  PersonIcon,
  DashboardIcon,
  HomeIcon,
  ActivityLogIcon,
} from "@radix-ui/react-icons";
import { CreditCardIcon, LandmarkIcon, WalletIcon } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";
import { useNavigate, useLocation } from "react-router-dom";

const menu = [
  { name: "Home", path: "/", icon: <HomeIcon className="h-5 w-5" /> },
  {
    name: "Portfolio",
    path: "/portfolio",
    icon: <DashboardIcon className="h-5 w-5" />,
  },
  {
    name: "Watchlist",
    path: "/watchlist",
    icon: <BookmarkIcon className="h-5 w-5" />,
  },
  {
    name: "Activity",
    path: "/activity",
    icon: <ActivityLogIcon className="h-5 w-5" />,
  },
  { name: "Wallet", path: "/wallet", icon: <WalletIcon className="h-5 w-5" /> },
  {
    name: "Payment Details",
    path: "/payment-details",
    icon: <LandmarkIcon className="h-5 w-5" />,
  },
  {
    name: "Withdrawal",
    path: "/withdrawal",
    icon: <CreditCardIcon className="h-5 w-5" />,
  },
  {
    name: "Profile",
    path: "/profile",
    icon: <PersonIcon className="h-5 w-5" />,
  },
];

const SideBar = ({ isSheet }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { logout } = useAuth();

  const handleLogout = () => {
    console.log("[SideBar] logout triggered");
    logout();
    navigate("/");
  };

  const SidebarItem = ({ item }) => {
    const isActive = location.pathname === item.path;
    
    const content = (
      <div
        onClick={() => navigate(item.path)}
        className={`flex items-center gap-3 px-3 py-2.5 rounded-input transition-all duration-150 cursor-pointer font-medium text-sm
          ${isActive 
            ? "bg-brand-light dark:bg-brand-dark/20 text-brand-primary font-semibold" 
            : "text-app-textSecondary dark:text-gray-400 hover:bg-brand-light dark:hover:bg-brand-dark/10 hover:text-brand-primary"
          }`}
      >
        <span>{item.icon}</span>
        <span>{item.name}</span>
      </div>
    );

    if (isSheet) {
      return (
        <SheetClose className="w-full text-left">
          {content}
        </SheetClose>
      );
    }

    return content;
  };

  return (
    <div className={`bg-white dark:bg-[#1a1a2e] h-full flex flex-col py-6 px-4 transition-colors ${!isSheet ? "border-r border-app-border dark:border-gray-800 w-64 min-h-screen" : ""}`}>
      {!isSheet && (
        <div className="mb-8 px-2">
          <img 
            src={import.meta.env.VITE_LOGO_URL || "/CoinDesk-logo.png"} 
            alt={import.meta.env.VITE_APP_NAME || "CoinDesk"} 
            className="h-10 lg:h-12 w-auto cursor-pointer object-contain"
            onClick={() => navigate("/")}
          />
        </div>
      )}

      <div className="space-y-1 flex-1">
        {menu.map((item) => (
          <SidebarItem key={item.path} item={item} />
        ))}
      </div>

      <div className="pt-4 border-t border-app-border dark:border-gray-800 mt-auto">
        <div
          onClick={handleLogout}
          className="flex items-center gap-3 px-3 py-2.5 rounded-input text-app-error hover:bg-red-50 dark:hover:bg-red-950/20 transition-all cursor-pointer font-medium text-sm"
        >
          <ExitIcon className="h-5 w-5" />
          <span>Logout</span>
        </div>
      </div>
    </div>
  );
};

export default SideBar;
