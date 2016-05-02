/**
 * Created by dima on 30.04.16.
 */

'use strict';

var app = angular.module('appAdmin', ['ui.router', 'appMain', 'appStaffManagement',
    'appStudentManagement', 'appScheduling', 'appReport', 'appHeader', 'appMenu', 'appFooter']);

app.config(function ($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise("/admin");

    $stateProvider
        .state('main', {
            url: "/main",
            templateUrl: "module/admin/main/main.html",
            controller: "mainController"
        })
        .state('staffmanagement', {
            url: "/staffmanagement",
            templateUrl: "module/admin/staffmanagement/staffmanagement.html",
            controller: "staffManagementController"
        })
        .state('studentmanagement', {
            url: "/studentmanagement",
            templateUrl: "module/admin/studentmanagement/studentmanagement.html",
            controller: "studentManagementController",
            // resolve: {
            //     students: function ($http) {
            //         return $http.post('/admin/getallstudent').then(function (response) {
            //             console.log(response);
            //             return  response;
            //         });
            //     }
            // }
            resolve: {
                students: function (studentManagementService) {
                   return  studentManagementService.loadStudents();
                }
            }
        })
        .state('scheduling', {
            url: "/scheduling",
            templateUrl: "module/admin/scheduling/scheduling.html",
            controller: "schedulingController"
        })
        .state('report', {
            url: "/report",
            templateUrl: "module/admin/report/report.html",
            controller: "reportController"
        });
});

app.directive('appMenu', function () {
    return {
        templateUrl: 'module/admin/menu/menu.html',
        controller: 'menuController'
    };
});

// app.directive('appStudentManagement', function () {
//     return {
//         templateUrl: 'module/admin/studentmanagement/studentmanagement.html',
//         controller: 'studentManagementController'
//     };
// });






