import { initFederation } from '@angular-architects/native-federation';

initFederation({
  productApp: 'http://localhost:4200/remoteEntry.json',
  orderApp: 'http://localhost:4200/remoteEntry.json',
  reportApp: 'http://localhost:4200/remoteEntry.json',
})
  .catch((err) => console.error(err))
  .then((_) => import('./bootstrap'))
  .catch((err) => console.error(err));
