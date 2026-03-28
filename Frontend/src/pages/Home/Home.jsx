/* eslint-disable no-unused-vars */
import { useEffect, useRef, useState } from "react";
import { AssetTable } from "./AssetTable";
import { Button } from "@/components/ui/button";
import StockChart from "../StockDetails/StockChart";
import {
  ChevronLeftIcon,
  Cross1Icon,
} from "@radix-ui/react-icons";
import { useAuth } from "../../contexts/AuthContext";
import { useCoins } from "../../contexts/CoinContext";
import { useChat } from "../../contexts/ChatContext";
import SpinnerBackdrop from "@/components/custom/SpinnerBackdrop";
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
} from "@/components/ui/pagination";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { Input } from "@/components/ui/input";
import { MessageCircle, TrendingUp, BarChart3, Activity } from "lucide-react";

const Home = () => {
  const [page, setPage] = useState(1);
  const [category, setCategory] = useState("all");
  
  const { coinList, top50, coinDetails, loading, getCoinList, fetchCoinDetails, getTop50Coins, getTrendingCoins } = useCoins();
  const { messages, loading: chatLoading, sendMessage } = useChat();
  const { user, jwt } = useAuth();

  const [isBotRelease, setIsBotRelease] = useState(false);

  useEffect(() => {
    getCoinList(page);
  }, [page]);

  useEffect(() => {
    fetchCoinDetails({
      coinId: "bitcoin",
      jwt: jwt || localStorage.getItem("jwt"),
    });
  }, []);

  useEffect(() => {
    if (category === "top50") {
      getTop50Coins();
    } else if (category === "trending") {
      getTrendingCoins();
    }
  }, [category]);

  const handlePageChange = (page) => {
    setPage(page);
  };

  const handleBotRelease = () => setIsBotRelease(!isBotRelease);

  const [inputValue, setInputValue] = useState("");

  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      sendMessage(
        jwt || localStorage.getItem("jwt"),
        inputValue
      );
      setInputValue("");
    }
  };

  const handleChange = (event) => {
    setInputValue(event.target.value);
  };

  const chatContainerRef = useRef(null);

  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  if (loading) {
    return <SpinnerBackdrop />;
  }

  return (
    <div className="bg-app-bg min-h-screen p-6">
      <div className="max-w-[1600px] mx-auto space-y-8">
        {/* Header Section */}
        <div>
          <h1 className="text-app-textPrimary font-bold text-2xl mb-1">Market Dashboard</h1>
          <p className="text-app-textSecondary text-sm mb-6">Track live cryptocurrency prices and trends</p>
        </div>

        {/* Stats Section */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white rounded-card shadow-card border border-app-border p-5 flex items-center gap-4">
            <div className="bg-brand-light rounded-full p-2.5">
              <TrendingUp className="text-brand-primary h-6 w-6" />
            </div>
            <div>
              <p className="text-app-textSecondary text-xs font-medium uppercase tracking-wide">Top Performer</p>
              <p className="text-app-textPrimary font-bold text-xl mt-1">{coinList[0]?.name || "N/A"}</p>
            </div>
          </div>
          <div className="bg-white rounded-card shadow-card border border-app-border p-5 flex items-center gap-4">
            <div className="bg-brand-light rounded-full p-2.5">
              <BarChart3 className="text-brand-primary h-6 w-6" />
            </div>
            <div>
              <p className="text-app-textSecondary text-xs font-medium uppercase tracking-wide">Active Assets</p>
              <p className="text-app-textPrimary font-bold text-xl mt-1">{coinList?.length || 0} Coins</p>
            </div>
          </div>
          <div className="bg-white rounded-card shadow-card border border-app-border p-5 flex items-center gap-4">
            <div className="bg-brand-light rounded-full p-2.5">
              <Activity className="text-brand-primary h-6 w-6" />
            </div>
            <div>
              <p className="text-app-textSecondary text-xs font-medium uppercase tracking-wide">Market Sentiment</p>
              <p className="text-brand-primary text-sm font-semibold mt-1 flex items-center gap-1">
                Bullish <span className="text-xs font-normal text-app-textSecondary"> (24h)</span>
              </p>
            </div>
          </div>
        </div>

        <div className="lg:flex gap-8">
          {/* Main Table Section */}
          <div className="lg:w-[65%] space-y-6">
            <div className="flex items-center justify-between">
              <h2 className="text-app-textPrimary font-bold text-lg">Market Assets</h2>
              <div className="flex items-center gap-2 p-1 bg-white border border-app-border rounded-pill">
                <Button
                  variant="ghost"
                  onClick={() => setCategory("all")}
                  className={`rounded-pill px-4 h-8 text-sm ${category === "all" ? "bg-brand-primary text-white hover:bg-brand-dark" : "text-app-textSecondary"}`}
                >
                  All Assets
                </Button>
                <Button
                  variant="ghost"
                  onClick={() => setCategory("top50")}
                  className={`rounded-pill px-4 h-8 text-sm ${category === "top50" ? "bg-brand-primary text-white hover:bg-brand-dark" : "text-app-textSecondary"}`}
                >
                  Top 50
                </Button>
              </div>
            </div>

            <AssetTable
              category={category}
              coins={category === "all" ? coinList : top50}
            />

            {category === "all" && (
              <div className="flex justify-center pt-4">
                <Pagination>
                  <PaginationContent className="bg-white border border-app-border rounded-pill p-1">
                    <PaginationItem>
                      <Button
                        variant="ghost"
                        disabled={page === 1}
                        onClick={() => handlePageChange(page - 1)}
                        className="text-app-textSecondary rounded-pill"
                      >
                        <ChevronLeftIcon className="h-4 w-4" />
                      </Button>
                    </PaginationItem>
                    <PaginationItem>
                      <PaginationLink
                        onClick={() => handlePageChange(1)}
                        isActive={page === 1}
                        className={page === 1 ? "bg-brand-primary text-white hover:bg-brand-dark rounded-pill" : "rounded-pill"}
                      >
                        1
                      </PaginationLink>
                    </PaginationItem>
                    {page > 2 && <PaginationItem><PaginationEllipsis /></PaginationItem>}
                    {page > 1 && page < 50 && (
                      <PaginationItem>
                        <PaginationLink isActive className="bg-brand-primary text-white hover:bg-brand-dark rounded-pill">
                          {page}
                        </PaginationLink>
                      </PaginationItem>
                    )}
                    <PaginationItem><PaginationEllipsis /></PaginationItem>
                    <PaginationItem>
                      <PaginationNext
                        className="cursor-pointer text-app-textSecondary rounded-pill"
                        onClick={() => handlePageChange(page + 1)}
                      />
                    </PaginationItem>
                  </PaginationContent>
                </Pagination>
              </div>
            )}
          </div>

          {/* Side Details/Chart Section */}
          <div className="hidden lg:block lg:w-[35%] space-y-6">
            <div className="bg-white rounded-card shadow-card border border-app-border p-6 space-y-6">
              <h2 className="text-app-textPrimary font-bold text-lg">Live Analysis</h2>
              <div className="h-[300px]">
                <StockChart coinId={"bitcoin"} />
              </div>
              <div className="flex items-center justify-between border-t border-app-border pt-6">
                <div className="flex items-center gap-3">
                  <Avatar className="h-10 w-10 ring-2 ring-brand-light">
                    <AvatarImage src={coinDetails?.image?.large} />
                  </Avatar>
                  <div>
                    <p className="text-app-textPrimary font-bold leading-tight">{coinDetails?.name}</p>
                    <p className="text-app-textSecondary text-xs uppercase">{coinDetails?.symbol}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-app-textPrimary font-bold text-lg">
                    ${coinDetails?.market_data.current_price.usd.toLocaleString()}
                  </p>
                  <p
                    className={`text-sm font-semibold ${
                      coinDetails?.market_data.price_change_percentage_24h < 0
                        ? "text-app-error"
                        : "text-brand-primary"
                    }`}
                  >
                    {coinDetails?.market_data.price_change_percentage_24h?.toFixed(2)}%
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Floating Chatbot Section */}
      <section className="fixed bottom-6 right-6 z-50 flex flex-col items-end gap-3">
        {isBotRelease && (
          <div className="bg-white rounded-card shadow-card border border-app-border w-[350px] md:w-[400px] h-[550px] flex flex-col overflow-hidden">
            <div className="bg-brand-primary px-6 py-4 flex justify-between items-center">
              <div className="flex items-center gap-2">
                <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
                <p className="text-white font-bold text-sm">CoinDesk AI</p>
              </div>
              <Button onClick={handleBotRelease} size="icon" variant="ghost" className="text-white hover:bg-white/10 rounded-full h-8 w-8 p-0">
                <Cross1Icon className="h-4 w-4" />
              </Button>
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-app-bg scroll-container">
              <div className="flex flex-col gap-1 items-start max-w-[85%]">
                <div className="bg-white border border-app-border px-4 py-3 rounded-2xl rounded-tl-none shadow-sm text-sm text-app-textPrimary">
                  Hi {user?.fullName?.split(" ")[0]}, I'm your crypto assistant. Ask me anything about the market!
                </div>
              </div>

              {messages.map((item, index) => (
                <div
                  key={index}
                  className={`flex flex-col gap-1 max-w-[85%] ${item.role === "user" ? "items-end self-end ml-auto" : "items-start"}`}
                >
                  <div className={`px-4 py-3 rounded-2xl text-sm shadow-sm ${
                    item.role === "user" 
                      ? "bg-brand-primary text-white rounded-tr-none" 
                      : "bg-white border border-app-border text-app-textPrimary rounded-tl-none"
                  }`}>
                    {item.role === "user" ? item.prompt : item.ans}
                  </div>
                </div>
              ))}
              <div ref={chatContainerRef} />
              {chatLoading && (
                <div className="flex gap-1 items-center px-4">
                  <div className="w-1.5 h-1.5 bg-brand-primary rounded-full animate-bounce"></div>
                  <div className="w-1.5 h-1.5 bg-brand-primary rounded-full animate-bounce [animation-delay:0.2s]"></div>
                  <div className="w-1.5 h-1.5 bg-brand-primary rounded-full animate-bounce [animation-delay:0.4s]"></div>
                </div>
              )}
            </div>

            <div className="p-4 bg-white border-t border-app-border">
              <div className="flex items-center gap-2 bg-app-bg border border-app-border rounded-pill px-4 py-1 focus-within:ring-2 focus-within:ring-brand-primary/20 transition-all">
                <Input
                  className="bg-transparent border-none focus-visible:ring-0 text-sm h-10 px-0"
                  placeholder="Ask a question..."
                  onChange={handleChange}
                  value={inputValue}
                  onKeyPress={handleKeyPress}
                />
                <Button 
                  onClick={() => {
                    sendMessage(jwt || localStorage.getItem("jwt"), inputValue);
                    setInputValue("");
                  }} 
                  className="rounded-full h-8 w-8 p-0 bg-brand-primary"
                >
                  <TrendingUp className="h-4 w-4" />
                </Button>
              </div>
            </div>
          </div>
        )}
        <Button
          onClick={handleBotRelease}
          className="rounded-full h-14 w-14 shadow-card bg-brand-primary hover:bg-brand-dark p-0 flex items-center justify-center transition-all hover:scale-105 active:scale-95"
        >
          <MessageCircle className="h-7 w-7 text-white" />
        </Button>
      </section>
    </div>
  );
};

export default Home;
