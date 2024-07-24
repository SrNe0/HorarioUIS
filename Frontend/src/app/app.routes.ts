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
import { loginGuard } from './guards/login.guard';

export const routes: Routes = [
    {
        path: 'login',
        title: 'Login',
        component: LoginComponent
    },
    {
        path: 'home',
        title: 'Inicio',
        component: HomeComponent,
        canActivate: [loginGuard]
    },
    {
        path: 'usuario',
        component: ContentComponent,
        canActivate: [loginGuard],
        canActivateChild: [loginGuard],
        children: [
            {
                path: 'asignaturas',
                title: 'Asignaturas',
                component: AsignaturasComponent
            },
            {
                path: 'aulas',
                title: 'Aulas',
                component: AulasComponent
            },
            {
                path: 'edificios',
                title: 'Edificios',
                component: EdificiosComponent
            },
            {
                path: 'usuarios',
                title: 'Usuarios',
                component: UsuariosComponent
            },
            {
                path: 'grupos',
                title: 'Grupos',
                component: GruposComponent
            },
            {
                path: 'horario',
                title: 'Horario',
                component: HorarioComponent
            },
            {
                path: 'profesores',
                title: 'Profesores',
                component: ProfesoresComponent
            },
            {
                path: 'reportes',
                title: 'Reportes',
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
