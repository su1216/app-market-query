#!/bin/bash

jks_path=$(dirname "$0")"/app/assistant.jks"
scheme="1"

_help() {
  echo "  sign_all -a alias -p password [-v scheme] -- [dir|apk_file]"
}

sign() {
  filename=$(basename "$1")
  dirname=$(dirname "$1")
  dest="${dirname}/signed/${filename%.*}-signed.apk"

  if [[ ${scheme} = "1" ]]; then
    jarsigner -storepass "$password" -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore "$jks_path" -signedjar "${dest}" "$1" "$alias"
  else
    apksigner.bat sign --ks "$jks_path" --ks-key-alias "$alias" --key-pass "pass:$password" --ks-pass "pass:$password" --out "${dest}" "$1"
  fi
}

sign_files() {
  for f in $(ls "$1"); do
    if [[ "$f" = *".apk" ]]; then
      sign "$1/$f"
    fi
  done
}

mk_dir() {
  if [[ -d "$1" ]]; then
    parent="$1"
  else
    parent=$(dirname "$1")
  fi
  (cd "$parent" && rm -r signed > /dev/null 2>&1 ; mkdir -p signed)
}

getopt -Q -o p:a:v: --long password:,alias:scheme -- "$@"
while [[ -n "$1" ]]; do
  case "$1" in
  -p|--password) password=$2
      shift;;
  -a|--alias) alias=$2
      shift;;
  -v) scheme=$2
      shift;;
  --) shift
      break;; #跳出while，而不是case
  *)
      _help
      exit 0
  ;;
  esac
  shift
done

if [[ -n "$1" ]]; then
  mk_dir "$1"
  if [[ -d "$1" ]]; then
    sign_files "$1"
  elif [[ "$1" = *".apk" ]]; then
      sign "$1"
  else
      _help
  fi
fi
