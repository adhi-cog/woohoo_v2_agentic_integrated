# Single image, multiple entrypoints — compose selects the service via `command`.
#
# Note on dependencies: `tsx` is a RUNTIME dependency here, not a dev one, because the
# services are executed directly from TypeScript (`npm run gateway` → `tsx …/main.ts`).
# Installing with --omit=dev while tsx sat in devDependencies produced an image that
# built cleanly and then crashed on start with "tsx: not found".

# Workspace manifests, collected rather than listed.
#
# The previous version named ten of them by hand. Five more workspaces were added later,
# nobody updated this file, and npm ci resolved the lockfile without them - so the image
# built clean and then died on start with "Cannot find package 'pg'", a dependency of a
# workspace Docker had never copied. mock-honeywell sat unhealthy for 39 hours on the
# same cause. A hand-maintained list of workspaces goes stale in silence.
#
# Each RUN is one line on purpose. This file gets edited through tooling that has eaten
# backslash-continuations more than once, and a half-joined RUN is a baffling build error.
FROM node:22-alpine AS manifests
WORKDIR /src
COPY . .
RUN mkdir -p /manifests
RUN find . -name package.json -not -path '*/node_modules/*' -print > /tmp/manifests.txt
RUN while read -r f; do mkdir -p "/manifests/$(dirname "$f")"; cp "$f" "/manifests/$f"; done < /tmp/manifests.txt
RUN cp package-lock.json /manifests/
RUN echo "manifests collected: $(find /manifests -name package.json | wc -l)"

FROM node:22-alpine AS deps
WORKDIR /app

# Only the manifests, so this layer and the npm ci below stay cached until a dependency
# actually changes rather than on every source edit.
COPY --from=manifests /manifests/ ./

# `npm ci` is reproducible and fails loudly on a lockfile mismatch - which is what you
# want in a build. Do not add a `|| npm install` fallback: it converts a real problem
# into a silently different dependency tree.
RUN npm ci --omit=dev --no-audit --no-fund

# Fail the build here rather than at container start. The runtime imports these through
# tsx, where a missing workspace dependency surfaces as a crash loop that reads like a
# code fault instead of a packaging one.
RUN node --input-type=module -e "await Promise.all(['pg','ws','zod','tsx'].map(m => import(m)))"
RUN echo "runtime dependencies resolve"

FROM node:22-alpine AS runtime
WORKDIR /app
ENV NODE_ENV=production

# tini gives us correct signal handling so `docker compose down` stops cleanly
# instead of waiting out the 10s kill timeout.
RUN apk add --no-cache tini curl

COPY --from=deps /app/node_modules ./node_modules
COPY . .

USER node
EXPOSE 8443

# $PORT, not a hardcoded 8443. One image runs several services: mock-honeywell listens
# on 8091 and was reported unhealthy for 39 hours while serving 200s, because the probe
# was pinned to the gateway's port. A health signal that is wrong is worse than none -
# it trains you to ignore it.
#
# One line, and comments above rather than inside it: a comment between a continued
# instruction's lines is not something to rely on a parser to handle.
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 CMD curl -fsS "http://127.0.0.1:${PORT:-8443}/health" || exit 1

ENTRYPOINT ["/sbin/tini", "--"]
CMD ["npm", "run", "gateway"]
