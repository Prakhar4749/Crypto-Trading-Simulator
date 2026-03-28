import { Avatar, AvatarImage } from "@/components/ui/avatar";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { useNavigate } from "react-router-dom";
import { useCoins } from "../../contexts/CoinContext";

export function AssetTable({ coins, category }) {
  const { coinList } = useCoins();
  const navigate = useNavigate();

  console.log("[AssetTable] coins rendered", { count: coinList?.length });

  return (
    <div className="bg-white rounded-card shadow-card border border-app-border overflow-hidden">
      <div className="overflow-x-auto">
        <Table>
        <TableHeader className="bg-app-bg">
          <TableRow className="hover:bg-transparent border-app-border">
            <TableHead className="py-4 px-6 text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Coin</TableHead>
            <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Symbol</TableHead>
            <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide hidden md:table-cell">Volume</TableHead>
            <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide hidden lg:table-cell">Market Cap</TableHead>
            <TableHead className="text-app-textSecondary text-xs font-semibold uppercase tracking-wide">24H</TableHead>
            <TableHead className="text-right px-6 text-app-textSecondary text-xs font-semibold uppercase tracking-wide">Price</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {coins.map((item) => (
            <TableRow
              className="cursor-pointer border-app-border hover:bg-brand-light transition-colors"
              onClick={() => navigate(`/market/${item.id}`)}
              key={item.id}
            >
              <TableCell className="px-6 py-4">
                <div className="flex items-center gap-3">
                  <Avatar className="h-8 w-8 ring-1 ring-app-border">
                    <AvatarImage src={item.image} alt={item.symbol} />
                  </Avatar>
                  <span className="text-app-textPrimary font-semibold text-sm"> {item.name}</span>
                </div>
              </TableCell>
              <TableCell className="text-app-textSecondary text-xs font-medium uppercase">
                {item.symbol}
              </TableCell>
              <TableCell className="text-app-textPrimary text-sm hidden md:table-cell">
                {item.total_volume.toLocaleString()}
              </TableCell>
              <TableCell className="text-app-textPrimary text-sm hidden lg:table-cell">
                {item.market_cap.toLocaleString()}
              </TableCell>
              <TableCell>
                <span
                  className={`px-2 py-1 rounded-pill text-xs font-bold ${
                    item.price_change_percentage_24h < 0
                      ? "bg-red-50 text-app-error"
                      : "bg-brand-light text-brand-primary"
                  }`}
                >
                  {item.price_change_percentage_24h?.toFixed(2)}%
                </span>
              </TableCell>
              <TableCell className="text-right px-6 text-app-textPrimary font-semibold">
                ${item.current_price.toLocaleString()}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      </div>
      </div>  );
}
