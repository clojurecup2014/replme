#!/bin/bash

if [ $# -eq 1 ]; then
    git clone "https://github.com/$1" git_out
    cd git_out
fi

/opt/lein repl :headless :host 0.0.0.0 :port 8081
