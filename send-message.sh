#!/bin/bash
cat resources/validate-request.json | amqp-publish -e 'validate' -p -r 'request' -C 'edeposit/epubcheck-validate' -C 'edeposit/epubcheck-validate' -E 'application/json' -u 'amqp://guest:guest@localhost/epubcheck'

cat resources/validate-request-with-sample-epub2.json | amqp-publish -e 'validate' -p -r 'request' -C 'edeposit/epubcheck-validate' -C 'edeposit/epubcheck-validate' -E 'application/json' -u 'amqp://guest:guest@localhost/epubcheck'

cat resources/validate-request-with-sample-epub3.json | amqp-publish -e 'validate' -p -r 'request' -C 'edeposit/epubcheck-validate' -C 'edeposit/epubcheck-validate' -E 'application/json' -u 'amqp://guest:guest@localhost/epubcheck'
