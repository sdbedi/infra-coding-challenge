# get absolute path of this file
CWD="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DOCKERFILES=${CWD}/../dockerfiles

docker-compose -f ${DOCKERFILES}/base.yml -f ${DOCKERFILES}/nodejs.yml down
docker-compose -f ${DOCKERFILES}/base.yml -f ${DOCKERFILES}/java.yml down

docker system prune --volumes