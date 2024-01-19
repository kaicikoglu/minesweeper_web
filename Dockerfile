FROM hseeberger/scala-sbt

COPY ./target/universal/minesweeper_web-1.0-SNAPSHOT.zip .

RUN unzip minesweeper_web-1.0-SNAPSHOT.zip && \
    ls minesweeper_web-1.0-SNAPSHOT/bin &&

WORKDIR minesweeper_web-1.0-SNAPSHOT

CMD["bin/minesweeper_web"]