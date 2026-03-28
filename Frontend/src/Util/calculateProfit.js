export const calculateProfit = (order) => {
    if (order && order.buyPrice && order.sellPrice) {
         return order.sellPrice - order.buyPrice;
    }
    return 0;
}
