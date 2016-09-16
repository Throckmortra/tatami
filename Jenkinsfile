node('tatami') {
    // uncomment these 2 lines and edit the name 'node-4.4.7' according to what you choose in configuration
    // TEST 
    def nodeHome = tool name: 'node-4.4.5', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
    env.PATH = "${nodeHome}/bin:${env.PATH}"

    stage 'check tools'
    sh "node -v"
    sh "npm -v"
    sh "bower -v"
    
    stage 'checkout'
    checkout scm

    stage 'npm install'
    sh "npm install"
    sh "npm update"
    sh "npm install -g grunt-cli"
    
    stage 'clean'
    sh "./mvnw -Pprod clean package"

    stage 'backend tests'
    sh "./mvnw test"

    stage 'frontend tests'
    sh "grunt test"

    // stage 'packaging'
    // sh "./mvnw package -Pprod -DskipTests"
    
    stage 'deploy'
    sh "cp target/*.war.original /var/lib/tomcat7/webapps/ROOT.war"
    sh "service tomcat7 restart"
}
