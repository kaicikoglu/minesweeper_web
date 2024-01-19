# Use the hseeberger/scala-sbt image as the base
FROM hseeberger/scala-sbt:11.0.12_1.5.5_2.13.6

# Set the working directory
WORKDIR /app

# Copy the necessary project files into the container
COPY . /app/

# Build your Play application
RUN sbt stage

# Expose the Play framework default port
EXPOSE 9000

# Start the Play application
CMD ["target/universal/stage/bin/minesweeper_web", "-Dhttp.port=9000","-Dplay.http.secret.key=H_`v1?T7^PFAx7bs[0pKLSSGTRAyxH><W5Xw@Glm<BMEyUKqBmPw9qTGXBlpwjOt"]
