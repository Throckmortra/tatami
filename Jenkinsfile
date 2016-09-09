node {
    // uncomment these 2 lines and edit the name 'node-4.4.7' according to what you choose in configuration
    def nodeHome = tool name: 'node-4.4.5', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
    env.PATH = "${nodeHome}/bin:${env.PATH}"

    stage 'check tools'
    sh "node -v"
    sh "npm -v"
    sh "bower -v"
    //sh "grunt -v"

    stage 'Cassandra'
    sh "docker-compose -f /workspace/src/main/docker/cassandra.yml up -d"
    
    stage 'checkout'
    checkout scm

    stage 'npm install'
    sh "npm install"
    
    stage 'clean'
    sh "./mvnw clean"

    stage 'backend tests'
    sh "./mvnw test"

    stage 'frontend tests'
    sh "grunt test"

    stage 'packaging'
    sh "./mvnw package -Pprod -DskipTests"
}
