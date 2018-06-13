setwd(dirname(parent.frame(2)$ofile))
library(plotly)

data <- read.csv("output.csv", sep = ";")
data$time <- data$time/60/60
data$numb_cars[is.na(data$numb_cars)] <- 0
data$avg_velo[is.na(data$avg_velo)] <- 0
data$frac_wait[is.na(data$frac_wait)] <- 0

plot_ly(x=data$time, y=data$avg_velo) %>%
#  add_markers(y = ~data$avg_velo) %>%
  add_lines(y = ~fitted(loess(data$avg_velo ~ data$time))) %>%
  layout(xaxis = list(title = 'Time (h)'),
         yaxis = list(title = 'Average Velocity'),
         legend = list(x = 0.80, y = 0.90))

plot_ly(x=data$time, y=data$numb_cars) %>%
  #  add_markers(y = ~data$avg_velo) %>%
  add_lines(y = ~fitted(loess(data$numb_cars ~ data$time))) %>%
  layout(xaxis = list(title = 'Time (h)'),
         yaxis = list(title = 'Number of Cars in System'),
         legend = list(x = 0.80, y = 0.90))

plot_ly(x=data$time, y=data$frac_wait) %>%
  #  add_markers(y = ~data$avg_velo) %>%
  add_lines(y = ~fitted(loess(data$frac_wait ~ data$time))) %>%
  layout(xaxis = list(title = 'Time (h)'),
         yaxis = list(title = 'Fractional Waiting Time'),
         legend = list(x = 0.80, y = 0.90))

