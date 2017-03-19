app.config(['$stateProvider', '$urlRouterProvider',function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/');
    $stateProvider.state('app', {
        url:'',
        views: {
//            header: {
//                templateUrl: 'views2/components/header.html',
//                Controller: function(){}
//            },
            sidebar: {
                templateUrl: 'views2/components/sidebar.html',
                Controller: 'SidebarCtrl',
            }
        }
    })
    .state('home', {
        url: '/',
        views: {
            content: {
                templateUrl: 'views2/auth/login.html',
                controller: 'LoginCtrl'
            }
        }
    })
    .state('login', {
        url: '/login',
        views: {
            content: {
                templateUrl: 'views2/auth/login.html',
                controller: 'LoginCtrl'
            }
        }
    })
    .state('app.backtest', {
        'abstract': !0,
        url: '/backtest'
    })
    .state('app.backtest.list', {
        url: '',
        views: {
            'content@': {
                templateUrl: 'views2/backtest/backtest.list.html',
                Controller: 'BacktestListCtrl'
            }
        }
    })
    .state('app.backtest.detail', {
        url: '/:backtest_id',
        views: {
            'content@': {
                templateUrl: 'views2/backtest/backtest.detail.html',
                controller: 'BacktestDetailCtrl'
            }
        }
    })
    .state('app.simtrade', {
        'abstract': !0,
        url: '/simtrade'
    })
    .state('app.simtrade.list', {
        url: '',
        views: {
            'content@': {
                templateUrl: 'views2/strategy/strategy.list.html',
                Controller: 'StrategyListCtrl'
            }
        }
    })
    .state('app.realtrade', {
        'abstract': !0,
        url: '/realtrade'
    })
    .state('app.realtrade.list', {
        url: '',
        views: {
            'content@': {
                templateUrl: 'views2/strategy/strategy.list.html',
                Controller: 'StrategyListCtrl'
            }
        }
    })
    .state('app.strategy', {
        'abstract': !0,
        url: '/strategy'
    })
    .state('app.strategy.list', {
        url: '',
        views: {
            'content@': {
                templateUrl: 'views2/strategy/strategy.list.html',
                Controller: 'StrategyListCtrl'
            }
        }
    })
    .state('app.strategy.detail', {
        url: '/:strategy_id',
        views: {
            'content@': {
                templateUrl: 'views2/strategy/strategy.detail.html',
                Controller: 'StrategyDetailCtrl'
            },
            'sidebar_right@': {
                templateUrl: 'views2/strategy/strategy.sidebar.html',
                Controller: 'StrategySidebarCtrl'
            }
        }
    })
    .state('app.account', {
        'abstract': !0,
        url: '/account'
    })
    .state('app.account.list', {
        url: '',
        views: {
            'content@': {
                templateUrl: 'views2/account/account.list.html',
                Controller: 'AccListCtrl'
            }
        }
    })
    .state('app.account.info', {
        url: '/:account_id',
        views: {
            'content@': {
                templateUrl: 'views2/account/account.info.html',
                Controller: 'AccInfoCtrl'
            }
        }
    })
    .state('app.analysis', {
        'abstract': !0,
        url: '/analysis'
    })
    .state('app.analysis.list', {
        url: '',
        views: {
            'content@': {
                templateUrl: 'views2/analysis/analysis.list.html',
                Controller: 'AnalysisListCtrl'
            }
        }
    })
    .state('app.analysis.detail', {
        url: '/:strategy_id',
        views: {
            'content@': {
                templateUrl: 'views2/analysis/analysis.list.html',
                Controller: 'AnalysisListCtrl'
            }
        }
    })
}])
;