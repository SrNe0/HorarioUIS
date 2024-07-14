import { Routes } from '@angular/router';

import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { AsignaturasComponent } from './pages/content/component/asignaturas/asignaturas.component';
import { AulasComponent } from './pages/content/component/aulas/aulas.component';
import { EdificiosComponent } from './pages/content/component/edificios/edificios.component';
import { GetionUsuariosComponent } from './pages/content/component/getion-usuarios/getion-usuarios.component';   
import { GruposComponent } from './pages/content/component/grupos/grupos.component';
import { HorarioComponent } from './pages/content/component/horario/horario.component';
import { ProfesoresComponent } from './pages/content/component/profesores/profesores.component';
import { ReportesComponent } from './pages/content/component/reportes/reportes.component';
import { ContentComponent } from './pages/content/content.component';

export const routes: Routes = [
    {
        path: 'login',
        title: 'Login',
        component: LoginComponent
    },
    {
        path: 'home',
        component: HomeComponent,
    },
    {
        path: 'usuario',
        component: ContentComponent,
        children: [
            {
                path: 'asignaturas',
                component: AsignaturasComponent
            },
            {
                path: 'aulas',
                component: AulasComponent
            },
            {
                path: 'edificios',
                component: EdificiosComponent
            },
            {
                path: 'gestion-de-usuarios',
                component: GetionUsuariosComponent
            },
            {
                path: 'grupos',
                component: GruposComponent
            },
            {
                path: 'horario',
                component: HorarioComponent
            },
            {
                path: 'profesores',
                component: ProfesoresComponent
            },
            {
                path: 'reportes',
                component: ReportesComponent
            }
        ]          
        
    },
    {
        path: '',
        redirectTo: '/login',
        pathMatch: 'full'
    },
    {
        path: '**',
        redirectTo: '/login',
        pathMatch: 'full'
    }
];
