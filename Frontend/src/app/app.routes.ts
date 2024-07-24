import { Routes } from '@angular/router';

import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { AsignaturasComponent } from './pages/content/components/asignaturas/asignaturas.component';
import { AulasComponent } from './pages/content/components/aulas/aulas.component';
import { EdificiosComponent } from './pages/content/components/edificios/edificios.component';
import { UsuariosComponent } from './pages/content/components/usuarios/usuarios.component';
import { GruposComponent } from './pages/content/components/grupos/grupos.component';
import { HorarioComponent } from './pages/content/components/horario/horario.component';
import { ProfesoresComponent } from './pages/content/components/profesores/profesores.component';
import { ReportesComponent } from './pages/content/components/reportes/reportes.component';
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
                component: UsuariosComponent
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
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: '**',
        redirectTo: 'login',
        pathMatch: 'full'
    }
];
