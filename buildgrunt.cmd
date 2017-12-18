echo off

:: Comprobar que esta instalado npm (Node.js)
cmd /c npm --version
IF NOT %ERRORLEVEL% == 0 (
	echo "Se necesita instalar Node.js (https://nodejs.org/download/)" & exit 1
)

:: Comprobar que este instalado 'grunt-cli'
cmd /c npm -g list grunt-cli
IF NOT %ERRORLEVEL% == 0 (
	cmd /c npm install -g grunt-cli
)

:: Comprobar/Instalar componentes del proyecto (package.json)
cmd /c npm install

:: Ejecutar script grunt (Gruntfile.js)
cmd /c grunt