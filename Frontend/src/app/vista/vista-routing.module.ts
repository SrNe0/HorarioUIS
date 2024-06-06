import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { UsuarioComponent } from './componentes/usuario/usuario.component';
import { HorarioComponent } from './componentes/horario/horario.component';
import { AsignaturasComponent } from './componentes/asignaturas/asignaturas.component';
import { GruposComponent } from './componentes/grupos/grupos.component';
import { ProfesoresComponent } from './componentes/profesores/profesores.component';
import { AulasComponent } from './componentes/aulas/aulas.component';
import { EdificiosComponent } from './componentes/edificios/edificios.component';
import { ReportesComponent } from './componentes/reportes/reportes.component';
import { GestuserComponent } from './componentes/gestuser/gestuser.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent, 
    children:[
      {
        path:'',
        redirectTo: 'servicios',
        pathMatch: 'full',
      },
      {
        path:'servicios',
        component: UsuarioComponent,
      },
      {
        path:'horario',
        component: HorarioComponent
      },
      {
        path:'asignaturas',
        component: AsignaturasComponent
      },
      {
        path:'grupos',
        component: GruposComponent
      },
      {
        path:'profesores',
        component: ProfesoresComponent
      },
      {
        path:'aulas',
        component: AulasComponent
      },
      {
        path:'edificios',
        component: EdificiosComponent
      },
      {
        path:'reportes',
        component: ReportesComponent
      },
      {
        path:'gestion+de+usuarios',
        component: GestuserComponent
      }

    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class VistaRoutingModule { }
