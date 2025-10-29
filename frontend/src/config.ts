// Конфигурация API endpoints
// Эти значения генерируются из config.env при запуске скрипта generate-configs.sh

// В production все запросы идут через Zuul Gateway
const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://localhost:8080';

export const config = {
    // Base URL для всех API запросов (Zuul Gateway)
    apiBaseUrl: API_BASE_URL,
    
    // Эндпоинты сервисов (через Gateway)
    humanBeingsApi: `${API_BASE_URL}/api/human-beings`,
    heroesApi: `${API_BASE_URL}/api/heroes`,
    
    // Прямые эндпоинты сервисов (для dev/debug)
    service1Direct: process.env.REACT_APP_SERVICE1_URL || 'https://localhost:8082',
    service2Direct: process.env.REACT_APP_SERVICE2_URL || 'https://localhost:8081',
    
    // Использовать Gateway или прямое подключение
    useGateway: process.env.REACT_APP_USE_GATEWAY !== 'false',
};

export default config;
