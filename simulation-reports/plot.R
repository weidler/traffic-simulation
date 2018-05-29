setwd(dirname(parent.frame(2)$ofile))
library(plotly)

data <- read.csv("output.csv", sep = ";")
data$time <- data$time/60/60

plot_ly(x=data$time, y=data$avg_velo) %>%
#  add_markers(y = ~data$avg_velo) %>%
  add_lines(y = ~fitted(loess(data$avg_velo ~ data$time))) %>%
  layout(xaxis = list(title = 'Time (h)'),
         yaxis = list(title = 'Average Velocity'),
         legend = list(x = 0.80, y = 0.90))