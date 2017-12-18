module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
	clean: {
		build: {
			src: ['WebRoot/build/']
		},
		buildtmp: {
			src: ['WebRoot/build/tmp/']
		}		
		
	},
	
	concat: {
	  css: {
		src: ['src/main/webapp/app/styles/modal.css', 'src/main/webapp/app/styles/main.css', 'src/main/webapp/app/styles/style.css', 'src/main/webapp/app/styles/loaders.css', 'src/main/webapp/app/styles/custom.css', 'src/main/webapp/app/styles/theme.blue.css', 'src/main/webapp/app/styles/bootstrap-switch.css', '!src/main/webapp/app/styles/styles.min.css'],
		dest: 'WebRoot/build/tmp/styles/styles.css',
	  },
	  cssvendor: {
		src: ['src/main/webapp/app/bower_components/bootstrap/dist/css/bootstrap.css', 
			'src/main/webapp/app/bower_components/jquery.gritter/css/jquery.gritter.css', 'src/main/webapp/app/bower_components/fontawesome/css/font-awesome.min.css', 
			'src/main/webapp/app/bower_components/nanoscroller/nanoscroller.css', 
			'src/main/webapp/app/bower_components/angular-busy/dist/angular-busy.min.css', 'src/main/webapp/app/bower_components/bootstrap-datepicker/css/datepicker.css', 
			'src/main/webapp/app/bower_components/scrollup/demo/css/themes/image.css', 'src/main/webapp/app/bower_components/textAngular/dist/textAngular.css'	
		  ],
		dest: 'WebRoot/build/tmp/styles/vendor.css',
	  }	  
	},	
	
	cssmin: {
	  options: {
		keepSpecialComments: 0
	  },
	  target: {
		files: {
		  'WebRoot/build/styles/styles.min.css': 'WebRoot/build/tmp/styles/styles.css',
		  'WebRoot/build/styles/vendor.min.css': 'WebRoot/build/tmp/styles/vendor.css'
		}
	  }
	},	
	
    ngAnnotate: {
        demo: {
            files: {
				'WebRoot/build/tmp/scripts/app.js': ['src/main/webapp/app/scripts/app.js'],
                'WebRoot/build/tmp/scripts/controllers.js': ['src/main/webapp/app/scripts/controllers/*.js'],
				'WebRoot/build/tmp/scripts/directives.js': ['src/main/webapp/app/scripts/directives/*.js'],
				'WebRoot/build/tmp/scripts/models.js': ['src/main/webapp/app/scripts/models/*.js'],
				'WebRoot/build/tmp/scripts/services.js': ['src/main/webapp/app/scripts/services/*.js'],
				'WebRoot/build/tmp/scripts/filters.js': ['src/main/webapp/app/scripts/filters/*.js']
            },
        }
    },
	
	uglify: {
		my_target: {
		  files: {
			'WebRoot/build/scripts/app.min.js': ['WebRoot/build/tmp/scripts/app.js', 'WebRoot/build/tmp/scripts/controllers.js', 'WebRoot/build/tmp/scripts/directives.js',
			'WebRoot/build/tmp/scripts/models.js', 'WebRoot/build/tmp/scripts/services.js', 'WebRoot/build/tmp/scripts/filters.js'],
			'WebRoot/build/scripts/vendor.min.js': ['src/main/webapp/app/bwip-js/bwip.js', 'src/main/webapp/app/bwip-js/lib/symdesc.js', 'src/main/webapp/app/bwip-js/lib/needyoffset.js', 'src/main/webapp/app/bwip-js/lib/canvas.js', 'src/main/webapp/app/scripts/bootstrap-switch.js']
		  }
		}
    },
	
	copy: {
		main: {
			files: [
			{expand: true, cwd: 'src/main/webapp/app/bower_components/bootstrap/dist/fonts/', src: ['**'], dest: 'WebRoot/build/fonts/'},
			{expand: true, cwd: 'src/main/webapp/app/bower_components/fontawesome/fonts/', src: ['**'], dest: 'WebRoot/build/fonts/'},
			{expand: true, cwd: 'src/main/webapp/app/webfont/', src: ['**'], dest: 'WebRoot/build/webfont'},
			{expand: true, cwd: 'WebRoot/build/scripts/', src: ['**'], dest: 'src/main/webapp/build/scripts'},
			{expand: true, cwd: 'WebRoot/build/styles/', src: ['**'], dest: 'src/main/webapp/build/styles'}
			]
		}	
	}
  });

  
  grunt.loadNpmTasks('grunt-ng-annotate');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-cssmin');
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-copy');
  

  // Default task(s).
  grunt.registerTask('default', ['clean:build','ngAnnotate','concat','uglify','cssmin','copy','clean:buildtmp']);

};