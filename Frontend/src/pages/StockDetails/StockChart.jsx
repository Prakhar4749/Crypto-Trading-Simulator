import { useEffect, useState } from "react";
import ReactApexChart from "react-apexcharts";
import { Button } from "@/components/ui/button";
import { useCoins } from "@/contexts/CoinContext";
import { useAuth } from "@/contexts/AuthContext";

const timeSeries = [
  { lable: "1D", value: 1 },
  { lable: "1W", value: 7 },
  { lable: "1M", value: 30 },
  { lable: "3M", value: 90 },
  { lable: "6M", value: 180 },
  { lable: "1Y", value: 365 },
];

const StockChart = ({ coinId }) => {
  const [activeType, setActiveType] = useState(timeSeries[0]);
  const { marketChart, getCoinMarketChart } = useCoins();
  const { jwt } = useAuth();

  const series = [
    {
      name: "Price",
      data: marketChart.data,
    },
  ];

  const options = {
    chart: {
      id: "area-datetime",
      type: "area",
      height: 450,
      zoom: {
        autoScaleYaxis: true,
      },
      toolbar: {
        show: false
      },
      fontFamily: 'Inter, sans-serif'
    },
    dataLabels: {
      enabled: false,
    },
    stroke: {
      curve: 'smooth',
      width: 2,
      colors: ['#00B386']
    },
    xaxis: {
      type: "datetime",
      labels: {
        style: {
          colors: '#6B7280',
          fontSize: '12px'
        }
      },
      axisBorder: {
        show: false
      },
      axisTicks: {
        show: false
      }
    },
    yaxis: {
      labels: {
        style: {
          colors: '#6B7280',
          fontSize: '12px'
        },
        formatter: (val) => `$${val.toLocaleString()}`
      }
    },
    colors: ["#00B386"],
    tooltip: {
      theme: "light",
      x: {
        format: "dd MMM yyyy"
      },
      style: {
        fontSize: '12px'
      }
    },
    fill: {
      type: "gradient",
      gradient: {
        shadeIntensity: 1,
        opacityFrom: 0.45,
        opacityTo: 0.05,
        stops: [0, 100],
      },
    },
    grid: {
      borderColor: "#E5E7EB",
      strokeDashArray: 4,
      show: true,
      padding: {
        top: 0,
        right: 0,
        bottom: 0,
        left: 10
      }
    },
  };

  useEffect(() => {
    if (coinId) {
      console.log("[StockChart] fetching chart", { coinId, days: activeType.value });
      getCoinMarketChart(coinId, activeType.value);
    }
  }, [coinId, activeType.value]);

  return (
    <div className="bg-white rounded-card shadow-card border border-app-border p-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
        <h2 className="text-app-textPrimary font-semibold text-lg">Price History</h2>
        <div className="flex flex-wrap items-center gap-2 p-1 bg-app-bg border border-app-border rounded-pill">
          {timeSeries.map((item) => (
            <Button
              key={item.lable}
              onClick={() => setActiveType(item)}
              variant="ghost"
              className={`h-7 px-4 text-xs font-semibold rounded-pill transition-all ${
                activeType.lable === item.lable 
                  ? "bg-brand-primary text-white hover:bg-brand-dark shadow-sm" 
                  : "text-app-textSecondary hover:bg-brand-light"
              }`}
            >
              {item.lable}
            </Button>
          ))}
        </div>
      </div>

      <div className="h-[450px] w-full relative">
        {marketChart.loading && (
          <div className="absolute inset-0 bg-white/50 backdrop-blur-[1px] flex items-center justify-center z-10">
            <div className="w-10 h-10 border-4 border-brand-light border-t-brand-primary rounded-full animate-spin"></div>
          </div>
        )}
        <ReactApexChart
          options={options}
          series={series}
          type="area"
          height={450}
        />
      </div>
    </div>
  );
};

export default StockChart;
