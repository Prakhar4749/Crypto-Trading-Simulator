import { Button } from "@/components/ui/button";
import {
  AvatarIcon,
  DragHandleHorizontalIcon,
  MagnifyingGlassIcon,
} from "@radix-ui/react-icons";
import SideBar from "../SideBar/SideBar";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { useNavigate, useLocation } from "react-router-dom";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { useAuth } from "../../contexts/AuthContext";
import { useTheme } from "../../contexts/ThemeContext";
import { BellIcon } from "@radix-ui/react-icons";
import { LogOut, Moon, Sun, User, Settings } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const { isDark, toggleTheme } = useTheme();

  console.log("[Navbar] rendered", { user });

  const navLinks = [
    { name: "Market", path: "/" },
    { name: "Portfolio", path: "/portfolio" },
    { name: "Watchlist", path: "/watchlist" },
    { name: "Wallet", path: "/wallet" },
  ];

  if (user?.role === "ROLE_ADMIN") {
    navLinks.push({ name: "Admin Panel", path: "/admin/withdrawal" });
  }

  return (
    <div className="bg-white dark:bg-[#1a1a2e] border-b border-app-border dark:border-gray-800 shadow-sm px-6 py-3 flex items-center justify-between sticky top-0 z-50 transition-colors">
      <div className="flex items-center gap-8">
        <div className="flex items-center gap-3">
          <Sheet>
            <SheetTrigger asChild>
              <Button
                variant="ghost"
                size="icon"
                className="text-app-textSecondary dark:text-gray-400 hover:text-brand-primary"
              >
                <DragHandleHorizontalIcon className="h-6 w-6" />
              </Button>
            </SheetTrigger>
            <SheetContent
              side="left"
              className="w-64 p-0 border-r border-app-border dark:border-gray-800 bg-white dark:bg-[#1a1a2e]"
            >
              <SheetHeader className="p-6 border-b border-app-border dark:border-gray-800">
                <SheetTitle className="text-left">
                  <img 
                    src={import.meta.env.VITE_LOGO_URL || "/CoinDesk-logo.png"} 
                    alt={import.meta.env.VITE_APP_NAME || "CoinDesk"} 
                    className="h-10 w-auto cursor-pointer"
                    onClick={() => navigate("/")}
                  />
                </SheetTitle>
              </SheetHeader>
              <SideBar isSheet={true} />
            </SheetContent>
          </Sheet>

          <img 
            src={import.meta.env.VITE_LOGO_URL || "/CoinDesk-logo.png"} 
            alt={import.meta.env.VITE_APP_NAME || "CoinDesk"} 
            className="h-10 lg:h-12 w-auto cursor-pointer hidden lg:block object-contain"
            onClick={() => navigate("/")}
          />
          <img 
            src={import.meta.env.VITE_LOGO_URL || "/CoinDesk-logo.png"} 
            alt={import.meta.env.VITE_APP_NAME || "CoinDesk"} 
            className="h-10 w-auto cursor-pointer lg:hidden object-contain"
            onClick={() => navigate("/")}
          />
        </div>

        <nav className="hidden lg:flex items-center gap-6">
          {navLinks.map((link) => (
            <span
              key={link.path}
              onClick={() => navigate(link.path)}
              className={`font-medium text-sm transition-colors cursor-pointer pb-1 ${
                location.pathname === link.path
                  ? "text-brand-primary border-b-2 border-brand-primary"
                  : "text-app-textSecondary dark:text-gray-400 hover:text-brand-primary"
              }`}
            >
              {link.name}
            </span>
          ))}
        </nav>
      </div>

      <div className="flex items-center gap-4">
        <div className="hidden md:flex items-center gap-3 bg-app-bg dark:bg-[#0f0f1a] border border-app-border dark:border-gray-800 rounded-pill px-4 py-1.5 focus-within:border-brand-primary transition-all w-64">
          <MagnifyingGlassIcon className="h-4 w-4 text-app-textSecondary" />
          <input
            type="text"
            placeholder="Search assets..."
            className="bg-transparent border-none outline-none text-sm w-full text-app-textPrimary placeholder:text-app-textSecondary"
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                navigate(`/search`);
              }
            }}
            onClick={() => navigate("/search")}
          />
        </div>

        <Button variant="ghost" size="icon" className="text-app-textSecondary dark:text-gray-400 hover:text-brand-primary">
          <BellIcon className="h-5 w-5" />
        </Button>

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Avatar className="cursor-pointer h-9 w-9 ring-2 ring-brand-light dark:ring-brand-dark transition-all hover:ring-brand-primary">
              {user?.avatar ? (
                <AvatarImage src={user.avatar} />
              ) : (
                <AvatarFallback className="bg-brand-light dark:bg-brand-dark text-brand-primary text-xs font-bold">
                  {user?.fullName ? user.fullName[0].toUpperCase() : <AvatarIcon className="h-6 w-6" />}
                </AvatarFallback>
              )}
            </Avatar>
          </DropdownMenuTrigger>
          <DropdownMenuContent className="w-56 mt-2 dark:bg-[#1a1a2e] dark:border-gray-800" align="end">
            <DropdownMenuLabel className="font-normal">
              <div className="flex flex-col space-y-1">
                <p className="text-sm font-medium leading-none dark:text-white">{user?.fullName}</p>
                <p className="text-xs leading-none text-app-textSecondary dark:text-gray-400">{user?.email}</p>
              </div>
            </DropdownMenuLabel>
            <DropdownMenuSeparator className="dark:bg-gray-800" />
            <DropdownMenuItem onClick={() => navigate("/profile")} className="cursor-pointer dark:text-gray-300 dark:focus:bg-gray-800">
              <User className="mr-2 h-4 w-4" />
              <span>Profile</span>
            </DropdownMenuItem>
            <DropdownMenuItem onClick={toggleTheme} className="cursor-pointer dark:text-gray-300 dark:focus:bg-gray-800">
              {isDark ? (
                <>
                  <Sun className="mr-2 h-4 w-4" />
                  <span>Light Mode</span>
                </>
              ) : (
                <>
                  <Moon className="mr-2 h-4 w-4" />
                  <span>Dark Mode</span>
                </>
              )}
            </DropdownMenuItem>
            <DropdownMenuSeparator className="dark:bg-gray-800" />
            <DropdownMenuItem onClick={logout} className="cursor-pointer text-red-500 focus:text-red-500 dark:focus:bg-red-950/20">
              <LogOut className="mr-2 h-4 w-4" />
              <span>Logout</span>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </div>
  );
};

export default Navbar;
