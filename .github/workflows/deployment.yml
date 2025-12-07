name: Java CI/CD

on:
  workflow_dispatch:
    inputs:
      environment:
        description: "Select environment"
        type: choice
        required: true
        options:
          - dev
          - staging
          - prod
        default: dev

      deploy:
        description: "Deploy after build?"
        type: boolean
        required: true
        default: false

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Build with Maven
        run: mvn -B clean install --no-transfer-progress

      - name: Run tests
        run: mvn test

      - name: Show selected options
        run: |
          echo "Environment chosen: ${{ github.event.inputs.environment }}"
          echo "Deploy? ${{ github.event.inputs.deploy }}"

      - name: Deploy (only if checkbox = true)
        if: ${{ github.event.inputs.deploy == 'true' }}
        run: |
          echo "Deploying to environment: ${{ github.event.inputs.environment }}"
          # your deploy commands here
          # example:
          # scp target/app.jar user@server:/deploy/path/
