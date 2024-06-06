import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VistaRoutingModule } from './vista-routing.module';

import { HomeComponent } from './home/home.component';
import { HorarioComponent } from './componentes/horario/horario.component';
import { AsignaturasComponent } from './componentes/asignaturas/asignaturas.component';
import { GruposComponent } from './componentes/grupos/grupos.component';
import { ProfesoresComponent } from './componentes/profesores/profesores.component';
import { AulasComponent } from './componentes/aulas/aulas.component';
import { EdificiosComponent } from './componentes/edificios/edificios.component';
import { ReportesComponent } from './componentes/reportes/reportes.component';
import { GestuserComponent } from './componentes/gestuser/gestuser.component';
import { UsuarioComponent } from './componentes/usuario/usuario.component';
import { FormchagesComponent } from './componentes/formchages/formchages.component';


@NgModule({
  declarations: [
    HomeComponent,
    HorarioComponent,
    AsignaturasComponent,
    GruposComponent,
    ProfesoresComponent,
    AulasComponent,
    EdificiosComponent,
    ReportesComponent,
    GestuserComponent,
    UsuarioComponent,
    FormchagesComponent,

  ],
  imports: [
    CommonModule,
    VistaRoutingModule
  ]
})
export class VistaModule { }
