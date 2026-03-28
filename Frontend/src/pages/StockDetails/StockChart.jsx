import { useState, useEffect } from 'react';
import { useCoins } from '../../contexts/CoinContext';
import ReactApexChart from 'react-apexcharts';

const TIME_RANGES = [
  { label: '1D', days: 1 },
  { label: '1W', days: 7 },
  { label: '1M', days: 30 },
  { label: '3M', days: 90 },
  { label: '1Y', days: 365 },
];

export default function StockChart({ coinId }) {
  const { getCoinChart } = useCoins();
  const [chartData, setChartData] = useState([]);
  const [selectedRange, setSelectedRange] = useState(TIME_RANGES[1]); // Default 1W
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (coinId) {
      loadChart(selectedRange.days);
    }
  }, [coinId, selectedRange]);

  const loadChart = async (days) => {
    setLoading(true);
    try {
      const data = await getCoinChart(coinId, days);
      // CoinGecko returns: 
      // { prices: [[timestamp, price], ...] }
      if (data?.prices) {
        setChartData(data.prices.map(
          ([timestamp, price]) => ({
            x: new Date(timestamp),
            y: price
          })
        ));
      }
    } catch (error) {
      console.error('Chart error:', error);
    } finally {
      setLoading(false);
    }
  };

  const chartOptions = {
    chart: {
      type: 'area',
      background: 'transparent',
      toolbar: { show: false },
      zoom: { enabled: false },
    },
    theme: { mode: 'dark' },
    stroke: {
      curve: 'smooth',
      width: 2,
      colors: ['#00B386'],
    },
    fill: {
      type: 'gradient',
      gradient: {
        shadeIntensity: 1,
        opacityFrom: 0.3,
        opacityTo: 0.01,
        stops: [0, 100],
        colorStops: [{
          offset: 0,
          color: '#00B386',
          opacity: 0.3
        }, {
          offset: 100,
          color: '#00B386',
          opacity: 0
        }]
      }
    },
    xaxis: {
      type: 'datetime',
      labels: {
        style: { colors: '#9ca3af' },
        datetimeFormatter: {
          day: 'dd MMM',
          hour: 'HH:mm'
        }
      },
      axisBorder: { show: false },
      axisTicks: { show: false },
    },
    yaxis: {
      labels: {
        style: { colors: '#9ca3af' },
        formatter: (val) => 
          '$' + val.toLocaleString('en-US', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
          })
      }
    },
    grid: {
      borderColor: 'rgba(255,255,255,0.05)',
      strokeDashArray: 4,
    },
    tooltip: {
      theme: 'dark',
      x: { format: 'dd MMM HH:mm' },
      y: {
        formatter: (val) =>
          '$' + val.toLocaleString('en-US', {
            minimumFractionDigits: 2
          })
      }
    },
    dataLabels: { enabled: false },
  };

  return (
    <div className="bg-[#1a1a2e] rounded-xl p-4">
      
      {/* Time Range Selector */}
      <div className="flex gap-1 mb-4">
        {TIME_RANGES.map((range) => (
          <button
            key={range.label}
            onClick={() => setSelectedRange(range)}
            className={`px-3 py-1.5 
              rounded-lg text-xs font-medium 
              transition-colors
              ${selectedRange.label === range.label
                ? 'bg-[#00B386] text-white'
                : 'text-gray-400 hover:bg-white/5'
              }`}>
            {range.label}
          </button>
        ))}
      </div>

      {/* Chart */}
      {loading ? (
        <div className="h-64 flex items-center justify-center">
          <div className="w-8 h-8 border-2 border-[#00B386] border-t-transparent rounded-full animate-spin" />
        </div>
      ) : chartData.length > 0 ? (
        <ReactApexChart
          type="area"
          height={280}
          options={chartOptions}
          series={[{ 
            name: 'Price', 
            data: chartData 
          }]}
        />
      ) : (
        <div className="h-64 flex items-center justify-center text-gray-500 text-sm">
          No chart data available
        </div>
      )}
    </div>
  );
}
