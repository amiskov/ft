#!/usr/bin/env sh

# abort on errors
set -e

# build
npm run clean && npm run release

# navigate into the build output directory
cd public

# if you are deploying to a custom domain
# echo 'oggetto.academy' > CNAME

git init
git add -A
git commit -m 'deploy'

# if you are deploying to https://<USERNAME>.github.io/<REPO>
git push -f git@github.com:amiskov/ft.git master:gh-pages

cd -