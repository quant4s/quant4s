app.controller('accountCtrl', ['$scope', '$state', '$http', '$uibModal', 'accountService', function($scope, $state, $http, $uibModal, accountService) {
            $scope.title = '实盘账户列表';
            accountService.list($http, $scope);
            $scope.addAccount = function() {
                $scope.account={};
            };
            $scope.clickAccount = function(row) {
                var modalInstance= $uibModal.open({
                    templateUrl:"views/account/account.connect.confirm.html",
                    controller:"accountDetailCtrl",
                    backdrop:"static",
                    resolve:{
                        acc: function(){
                            return row;
                        }
                    },
                });
            };
            $scope.isConnected = function(account) {
                return false;
            };
            $scope.addAccount = function() {
                var modalInstance= $uibModal.open({
                    templateUrl:"views/account/account.setting.html",
                    controller:"accountDetailCtrl",
                    controllerAs:"modal",
                    backdrop:"static",
                    resolve:{
                        acc:function(){return {};}
                    }
                });
            };
            $scope.editAccount = function(account) {
                var modalInstance= $uibModal.open({
                    templateUrl:"views/account/account.setting.html",
                    controller:"accountDetailCtrl",
                    controllerAs:"modal",
                    backdrop:"static",
                    resolve:{
                        acc:function(){return account;}
                    }
                });
            };
            $scope.removeAccount = function(account) {
                var modalInstance= $uibModal.open({
                    templateUrl:"views/account/account.remove.confirm.html",
                    controller:"accountDetailCtrl",
                    controllerAs:"modal",
                    backdrop:"static",
                    resolve:{
                        acc:function(){return account;}
                    }
                });
            };
        }])
    .controller('accountDetailCtrl', ['$scope', '$uibModalInstance','$http', '$stateParams', 'accountService','acc', function($scope, $uibModalInstance, $http, $stateParams, accountService, acc) {
            $scope.title = '实盘账户';
            $scope.acc = acc;
            accountService.brokerChannelTypes($http, $scope);
            // accountService.find($http, $scope, $stateParams.id);
            $scope.saveAccount = function(account) {
                if(typeof $scope.acc.id === 'undefined') {
                    accountService.insert($http, $scope, $scope.acc);
                } else {
                     accountService.update($http, $scope, $scope.acc);
                }
                $uibModalInstance.close();
            };

            $scope.ok = function() {
                accountService.connect($http, $scope, $scope.acc)
                $uibModalInstance.close();
            };

            $scope.removeAccount = function(account) {
                accountService.remove($http, $scope, account);
                $uibModalInstance.close();
            }
        }])
    .controller('accountInfoCtrl', ['$scope', '$http', '$stateParams', 'accountService', function($scope, $http, $stateParams, accountService) {
            // accountInfoCtrl
        }]);

app.factory('accountService', function(){
       return {
         list: function($http, $scope){
           $http({
                 method: "GET",
                 url: appInfo.host + "/account/list"
             }).then(function(response) {
               $scope.accounts = response.data.traders;
           });
         },
         find: function($http, $scope, id){
             $scope.account = _.find($scope.accounts, function(account){ return account.id == id });
         },
         onClickAccount: function(acc) {
         },
         update: function($http, $scope, account) {
          $http.put(appInfo.host + "/account/" + account.id, account)
          .then(function(response) {
         }, function(resp){
            alert("网络错误，状态" + resp.status);
         });

         },
         insert: function($http, $scope, account) {
            $http.post(appInfo.host + "/account",account).then(function(resp){

            }, function(resp){
                alert(resp.status);
            })
         },
         remove: function($http, $scope,account) {
            $http.delete( appInfo.host + "/account/" + account.id)
            .then(function(resp){
                // TODO: $scope.accounts.r
            }, function(resp){
                alert(resp.status);
            });
         },
         connect: function($http, $scope, account) {
           $http.put( appInfo.host + "/account/connect/" + account.id)
            .then(function(resp){
                // TODO: $scope.accounts.r
                alert("连接成功" + resp.status);
            }, function(resp){
                alert("连接失败" + resp.status);
            });
         },

        brokerChannelTypes:function($http, $scope) {
            $http({
                 method: "GET",
                 url: appInfo.host + "/config/brokerageChannelTypes"
            }).then(function(response) {
               $scope.brokerChannelTypes = response.data.channelTypes;
            });
        },


     };
   });