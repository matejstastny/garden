### Development notes

Example of flag script:

```bash
#!/bin/sh

set -eu

# @getoptions
parser_definition() {
  setup   REST help:usage -- "Usage: example.sh [options]... [arguments]..." ''
  msg -- 'Options:'
  flag    FLAG    -f --flag                 -- "takes no arguments"
  param   PARAM   -p --param                -- "takes one argument"
  option  OPTION  -o --option on:"default"  -- "takes one optional argument"
  disp    :usage  -h --help
  disp    VERSION    --version
}
# @end

# @gengetoptions parser -i parser_definition parse
#
#     INSERTED HERE
#
# @end

parse "$@"
eval "set -- $REST"

echo "FLAG: $FLAG, PARAM: $PARAM, OPTION: $OPTION"
printf '%s\n' "$@" # rest arguments
```
