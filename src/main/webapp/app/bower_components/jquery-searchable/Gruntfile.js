/*jshint node: true */

'use strict';

module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        jshint: {
            files: [
                'jquery.searchable.js',
                'Gruntfile.js'
            ],
            options: {
                jshintrc: true
            }
        },

        uglify: {
            options: {
                banner: '/*! <%= pkg.title %> v<%= pkg.version %> ' +
                        'by <%= pkg.author.name %> (<%= pkg.author.url %>) ' +
                        '| <%= pkg.license %> */\n'
            },
            min: {
                files: {
                    'dist/jquery.searchable-<%= pkg.version %>.min.js': 'jquery.searchable.js'
                }
            }
        },

        jscs: {
            all: {
                options: {
                    preset: 'jquery'
                },
                src: 'jquery.searchable.js'
            }
        },

        watch: {
            files: [ 'jquery.searchable.js' ],
            tasks: 'default'
        }
    });

    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-jscs-checker');

    grunt.registerTask('default', [ 'jscs', 'jshint', 'uglify' ]);
};
