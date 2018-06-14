#!/usr/bin/env bash
echo "rmarkdown::render('simulation-reports/report.Rmd', clean=TRUE, output_format='html_document', 'html-reports/$1.html')" | R --slave --args csv-data/$1.csv;