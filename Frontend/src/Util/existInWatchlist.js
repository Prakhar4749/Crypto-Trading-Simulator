export const existInWatchlist = (items, coin) => {
  if (!items || !coin) return false;
  return items.some(item => 
    (item?.id === coin?.id) || (typeof item === 'string' && item === coin?.id)
  );
};