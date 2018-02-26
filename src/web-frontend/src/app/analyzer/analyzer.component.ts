import { Component, OnInit, ViewChild, ElementRef, Inject } from '@angular/core';
import * as c3                                      from "c3";

import { Headers, Http }                            from '@angular/http';

import { Tank }                                     from '../model/tank'

@Component({
  selector: 'app-analyzer',
  templateUrl: './analyzer.component.html',
  styleUrls: ['./analyzer.component.css']
})
export class AnalyzerComponent implements OnInit {
  private static baseUrl = "http://localhost/rest/monitor-orchestrator-web/webresources/";

  @ViewChild('dataContainer') private dataContainer: ElementRef;

  tank1: boolean = true;
  tank2: boolean;
  tank3: boolean;
  tank4: boolean;
  tank5: boolean;

  Tank: boolean = true;

  constructor(private http: Http) { }

  ngOnInit() {
    this.refreshData();
  }

  public createPlot(chartName : string, columns : any[], y_label: string) {
    return c3.generate({
       bindto: '#' + chartName,
       data: {
         x: 'Timestamp',
         columns: columns,
       },
       axis: {
         x: {
          type: 'timeseries',
          tick: {
              format: '%Y-%m-%d %H:%M:%S',
              rotate: 30
          }
        },
        y: {
            label: y_label
        },
      }
     });
  }

  private appendContainer(displayName, chartName) {
      this.dataContainer.nativeElement.innerHTML += '<h3>' + displayName +  '</h3><br><div id="' + chartName + '"></div>';
  }

  private clearContainer() {
      this.dataContainer.nativeElement.innerHTML = "";
  }

  private retrieveData(tank : string, type : any) {
    var url : string = AnalyzerComponent.baseUrl + type.name.toLowerCase() + "/" + tank;
    console.log(url);

    this.http
          .get(url)
          .subscribe(
            result => {
                this.applyData(result.json() as typeof type[], type);
            },
            error => {
              console.error(error)
            }
          );
  }

  refreshData() {
    var tanks : Set<string> = new Set();
    var topics : Set<any> = new Set();

    if (this.tank1) tanks.add("1");
    if (this.tank2) tanks.add("2");
    if (this.tank3) tanks.add("3");

    if (this.Tank) topics.add(Tank);

    this.clearContainer();

    tanks.forEach((tank) => {
      topics.forEach((topic) => {
        this.retrieveData(tank, topic);
      });
    });
  }

  private mergeAll(values : any[], value: any[]) {
    for(var key in value) {
        values[key].push(value[key]);
    }
  }

  applyData(data : any[], type : any) {
    // try {
      var tanks : Set<String> = new Set();
      data.filter((elem) => tanks.add(elem.tankId));

      tanks.forEach((tank) => {

        var i : number = 0;
        type.plots.forEach((plot) => {

          var chartName : string = 'chart_' + tank + '_' + type.name + '_' + String(i);

          var columns = new Array<Array<any>>();
          for (var label in plot.labels) {
            columns.push(new Array<any>(plot.labels[label]));
          }

          data  .filter((elem) => elem.tankId == tank)        // filter for selected tank
                .sort((e1, e2) => e1.timestamp - e2.timestamp)      // sort by timestamp
                .map((measure) => {

                this.mergeAll(columns, plot.toArray(measure));

                // x_col.push(new Date(measure.timestamp));
          });
          console.log("Creating graph " + chartName);
          console.log(columns);

          this.appendContainer("Tank " + tank + " - " + plot.name, chartName);
          var buffer = this.createPlot(chartName, columns, plot.y_label);

          i += 1;
        });
      });
    // }
    // catch (Exception) {
    //   console.log('scope is ' + Exception);
    // }
  }
}
