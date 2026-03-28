import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { Input } from "@/components/ui/input";
import { SearchIcon } from "lucide-react";
import { useNavigate } from "react-router-dom";
import SpinnerBackdrop from "@/components/custom/SpinnerBackdrop";
import { useCoins } from "../../contexts/CoinContext";
import { useAssets } from "../../contexts/AssetsContext";
import { useOrder } from "../../contexts/OrderContext";

const SearchCoin = () => {
  const { searchCoinList, loading, searchCoins } = useCoins();
  const { userAssets } = useAssets();
  const { orders } = useOrder();
  
  const [keyword, setKeyword] = useState("");
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const value = e.target.value;
    setKeyword(value);
    if (value.trim().length > 1) {
      searchCoins(value);
    }
  };

  const handleSearchClick = () => {
    if (keyword.trim()) {
      searchCoins(keyword);
    }
  };

  return (
    <div className="container mx-auto p-6 md:p-10 min-h-screen bg-app-bg">
      <div className="flex flex-col items-center justify-center mb-12">
        <h1 className="text-2xl md:text-3xl font-bold text-app-textPrimary mb-6">Explore Crypto Market</h1>
        <div className="flex items-center w-full max-w-2xl bg-white dark:bg-[#1a1a2e] rounded-pill border border-app-border dark:border-gray-800 focus-within:border-brand-primary transition-all shadow-sm">
          <Input
            className="flex-1 border-none bg-transparent focus-visible:ring-0 focus-visible:ring-offset-0 px-6 py-6 text-base"
            placeholder="Search for a coin (e.g. bitcoin, ethereum)..."
            onChange={handleInputChange}
            value={keyword}
          />
          <Button 
            onClick={handleSearchClick} 
            className="bg-brand-primary hover:bg-brand-dark text-white rounded-pill m-1 px-6"
          >
            <SearchIcon className="h-5 w-5" />
          </Button>
        </div>
      </div>

      <div className="bg-white dark:bg-[#1a1a2e] rounded-card shadow-card border border-app-border dark:border-gray-800 overflow-hidden">
        {loading && !searchCoinList?.length ? (
          <div className="py-20 flex justify-center">
            <div className="w-10 h-10 border-4 border-brand-primary border-t-transparent rounded-full animate-spin" />
          </div>
        ) : searchCoinList?.length > 0 ? (
          <Table>
            <TableHeader className="bg-app-bg/50 dark:bg-gray-900/50">
              <TableRow>
                <TableHead className="w-[100px] text-center">Rank</TableHead>
                <TableHead>Coin</TableHead>
                <TableHead>Symbol</TableHead>
                <TableHead className="text-right">Action</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {searchCoinList.map((item) => (
                <TableRow
                  onClick={() => navigate(`/market/${item.id}`)}
                  key={item.id}
                  className="cursor-pointer hover:bg-app-bg/50 dark:hover:bg-gray-900/30 transition-colors"
                >
                  <TableCell className="text-center font-medium">
                    {item.market_cap_rank || "N/A"}
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-3">
                      <Avatar className="h-8 w-8 border border-app-border">
                        <AvatarImage src={item.large} alt={item.name} />
                      </Avatar>
                      <span className="font-semibold text-app-textPrimary"> {item.name}</span>
                    </div>
                  </TableCell>
                  <TableCell className="text-app-textSecondary uppercase">
                    {item.symbol}
                  </TableCell>
                  <TableCell className="text-right">
                    <Button variant="ghost" className="text-brand-primary hover:text-brand-dark hover:bg-brand-light/20">
                      View Details
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : (
          <div className="py-20 text-center text-app-textSecondary">
            {keyword ? "No coins found matching your search" : "Search results will appear here"}
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchCoin;
